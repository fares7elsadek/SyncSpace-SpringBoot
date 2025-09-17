package com.fares7elsadek.syncspace.server.commands.deleteserver;

import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.repository.ServerRepository;
import com.fares7elsadek.syncspace.server.shared.DeleteServerEvent;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteServerCommandHandler
        implements CommandHandler<DeleteServerCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;
    @Override
    @Transactional
    public ApiResponse<String> handle(DeleteServerCommand command) {
        var server = serverRepository.findById(command.serverId())
                .orElseThrow(() -> new ServerExceptions(String.format("Server with id %s not found", command.serverId())));

        var user = userAccessService.getCurrentUserInfo();

        var serverMember = serverMemberRepository
                .findById(new ServerMemberId(server.getId(),user.getId()))
                .orElseThrow(() -> new ServerExceptions(
                        String.format("User %s is not a member of server %s", user.getId(), server.getId())
                ));

        if(!serverMember.getRole().getName().equals(ServerRoles.OWNER.name()))
            throw new ServerExceptions(String.format("You don't have access to delete this server", server.getId()));

        serverMemberRepository.delete(serverMember);
        springEventPublisher
                .publish(new DeleteServerEvent(server.getId(),server.getName(),user.getId()));


        return ApiResponse
                .success(String.format("Server deleted successfully with id %s ", server.getId()),null);
    }
}
