package com.fares7elsadek.syncspace.server.application.commands.createserver;

import com.fares7elsadek.syncspace.server.application.services.ServerAvatarService;
import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.server.domain.model.ServerMember;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.server.domain.events.CreateServerEvent;
import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import com.fares7elsadek.syncspace.user.domain.model.Roles;
import com.fares7elsadek.syncspace.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("createServerCommandHandler")
@RequiredArgsConstructor
public class CreateServerCommandHandler
        implements CommandHandler<CreateServerCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;
    private final ServerAvatarService serverAvatarService;

    @Override
    @Transactional
    public ApiResponse<String> handle(CreateServerCommand command) {

        var server = serverRepository.save(createServerEntity(command));
        var user = userAccessService.getCurrentUserInfo();
        var role = userAccessService.getRoleByName(ServerRoles.OWNER.name());
        var serverMember = createServerMemberEntity(server, user, role);
        serverMemberRepository.save(serverMember);

        springEventPublisher
                .publish(CreateServerEvent.toEvent(server));

        return ApiResponse
                .success(String.format("Server created successfully with id %s", server.getId()),null);
    }

    private Server createServerEntity(CreateServerCommand command) {
        return Server.builder()
                .description(command.description())
                .name(command.name())
                .iconUrl(serverAvatarService.generateAvatarUrl(UUID.randomUUID().toString()))
                .isPublic(true)
                .membersNumber(1).build();
    }

    private ServerMember createServerMemberEntity(Server server, User user, Roles role) {
        return ServerMember.builder()
                .id(new ServerMemberId(server.getId(), user.getId()))
                .server(server)
                .user(user)
                .role(role).build();
    }
}
