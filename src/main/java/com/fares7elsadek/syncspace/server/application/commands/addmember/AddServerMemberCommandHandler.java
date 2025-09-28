package com.fares7elsadek.syncspace.server.application.commands.addmember;

import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.server.domain.events.AddServerMemberEvent;
import com.fares7elsadek.syncspace.server.domain.model.ServerMember;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddServerMemberCommandHandler implements CommandHandler<AddServerMemberCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;
    private final ServerRepository serverRepository;
    @Override
    public ApiResponse<String> handle(AddServerMemberCommand command) {
        var server = serverRepository.findById(command.serverId())
                .orElseThrow(()-> new NotFoundException("Server not found"));

        var currentUser = userAccessService.getCurrentUserInfo();
        var serverMember = serverMemberRepository.findById(new ServerMemberId(command.serverId(), currentUser.getId()))
                .orElseThrow(()-> new NotFoundException("Member not found"));

        if(serverMember.getRole().getName().equals(ServerRoles.USER.name()))
            throw new UnauthorizedException(String.format("Only 'Owners' and 'Admins' can add members for server id %s ", command.serverId()));

        var user = userAccessService.getByUsername(command.username());

        serverMemberRepository.findById(new ServerMemberId(command.serverId(), user.getId()))
                .ifPresent(serverMember1 -> {
                    throw new ServerExceptions("Server is already member");
                });

        var newServerMember = ServerMember.builder()
                .id(new ServerMemberId(server.getId(), user.getId()))
                .server(server)
                .user(user)
                .role(userAccessService.getRoleByName(ServerRoles.USER.name()))
                .build();

        serverMemberRepository.save(newServerMember);

        springEventPublisher.publish(AddServerMemberEvent.toEvent(currentUser.getId(), user.getId(),server.getId()));

        return ApiResponse.success("Member added successfully",null);
    }
}
