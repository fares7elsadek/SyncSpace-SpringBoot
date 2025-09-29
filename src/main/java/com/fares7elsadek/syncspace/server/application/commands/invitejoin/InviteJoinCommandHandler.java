package com.fares7elsadek.syncspace.server.application.commands.invitejoin;

import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.server.domain.events.InviteJoinEvent;
import com.fares7elsadek.syncspace.server.domain.model.ServerMember;
import com.fares7elsadek.syncspace.server.domain.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerInvitesRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.infrastructure.repository.ServerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InviteJoinCommandHandler implements
        CommandHandler<InviteJoinCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;
    private final ServerInvitesRepository serverInvitesRepository;
    private final ServerRepository serverRepository;
    @Transactional
    @Override
    public ApiResponse<String> handle(InviteJoinCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();

        var invite = serverInvitesRepository.findByCode(command.code())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Invite code not found for code %s", command.code()))
                );

        if (invite.isExpired()) {
            throw new ServerExceptions(String.format("Invite code %s has expired", command.code()));
        }

        if (invite.getUses() >= invite.getMaxUses()) {
            throw new ServerExceptions(String.format("Invite code %s has reached its max uses", command.code()));
        }

        var server = invite.getServer();

        serverMemberRepository.findById(new ServerMemberId(server.getId(), currentUser.getId()))
                .ifPresent(member -> {
                    throw new ServerExceptions(
                            String.format("You are already joined to this server (%s)", server.getId())
                    );
                });

        if(server.getMembersNumber() == server.getMaxMembers()){
            throw new ServerExceptions("Server has reached it's maximum number of members");
        }

        // increment uses, not maxUses
        invite.setUses(invite.getUses() + 1);
        serverInvitesRepository.save(invite);

        var newServerMember = ServerMember.builder()
                .id(new ServerMemberId(server.getId(), currentUser.getId()))
                .server(server)
                .user(currentUser)
                .role(userAccessService.getRoleByName(ServerRoles.USER.name()))
                .build();

        server.setMembersNumber(server.getMembersNumber() + 1);

        serverMemberRepository.save(newServerMember);
        serverRepository.save(server);

        springEventPublisher.publish(InviteJoinEvent.toEvent(
                currentUser.getId(), currentUser.getId(), command.serverId()
        ));

        return ApiResponse.success(
                String.format("User joined the server (%s) successfully", server.getId()),
                null
        );
    }

}
