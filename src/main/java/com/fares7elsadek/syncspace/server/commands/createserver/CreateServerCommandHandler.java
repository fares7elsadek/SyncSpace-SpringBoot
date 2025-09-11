package com.fares7elsadek.syncspace.server.commands.createserver;

import com.fares7elsadek.syncspace.server.model.Server;
import com.fares7elsadek.syncspace.server.model.ServerMember;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.repository.ServerRepository;
import com.fares7elsadek.syncspace.server.shared.CreateServerEvent;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.Roles;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateServerCommandHandler
        implements CommandHandler<CreateServerCommand, ApiResponse<String>> {

    private final UserValidationService userValidationService;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;

    @Override
    @Transactional
    public ApiResponse<String> handle(CreateServerCommand command) {

        var server = serverRepository.save(createServerEntity(command));
        var user = userValidationService.getCurrentUserInfo();
        var role = userValidationService.getRoleByName(ServerRoles.OWNER.toString());
        var serverMember = createServerMemberEntity(server, user, role);
        serverMemberRepository.save(serverMember);

        springEventPublisher
                .publish(new CreateServerEvent(server.getId()
                        , server.getName(),user.getId()));

        return ApiResponse
                .success(String.format("Server created successfully with id %s ", server.getId()),null);
    }

    private Server createServerEntity(CreateServerCommand command) {
        return Server.builder()
                .description(command.description())
                .name(command.name())
                .isPublic(command.isPublic()).build();
    }

    private ServerMember createServerMemberEntity(Server server, User user, Roles role) {
        return ServerMember.builder()
                .server(server)
                .user(user)
                .role(role).build();
    }
}
