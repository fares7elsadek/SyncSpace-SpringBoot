package com.fares7elsadek.syncspace.server.application.commands.deleteserver;

import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.server.domain.events.DeleteServerEvent;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
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
                .orElseThrow(() -> new NotFoundException(String.format("Server with id %s not found", command.serverId())));

        var user = userAccessService.getCurrentUserInfo();

        var serverMember = serverMemberRepository
                .findById(new ServerMemberId(server.getId(),user.getId()))
                .orElseThrow(() -> new NotFoundException(
                        String.format("User %s is not a member of server %s", user.getId(), server.getId())
                ));

        if(!serverMember.getRole().getName().equals(ServerRoles.OWNER.name()))
            throw new UnauthorizedException(String.format("You don't have access to delete this server", server.getId()));

        serverMemberRepository.delete(serverMember);
        springEventPublisher
                .publish(DeleteServerEvent.toEvent(server));


        return ApiResponse
                .success(String.format("Server deleted successfully with id %s ", server.getId()),null);
    }
}
