package com.fares7elsadek.syncspace.server.commands.generateinvite;

import com.fares7elsadek.syncspace.server.model.ServerInvites;
import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.server.model.dtos.InviteCodeDto;
import com.fares7elsadek.syncspace.server.repository.ServerInvitesRepository;
import com.fares7elsadek.syncspace.server.repository.ServerMemberRepository;
import com.fares7elsadek.syncspace.server.shared.InviteCodeGeneratedEvent;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Component("generateInviteCodeCommandHandler")
@RequiredArgsConstructor
@Slf4j
public class GenerateInviteCodeCommandHandler
        implements CommandHandler<GenerateInviteCodeCommand, ApiResponse<InviteCodeDto>> {

    private final UserAccessService userAccessService;
    private final ServerMemberRepository serverMemberRepository;
    private final SpringEventPublisher springEventPublisher;
    private final ServerInvitesRepository serverInvitesRepository;
    @Override
    @Transactional
    public ApiResponse<InviteCodeDto> handle(GenerateInviteCodeCommand command) {
        // admins & Owners only can generate invite code
        var currentUser = userAccessService.getCurrentUserInfo();
        var serverMember = serverMemberRepository
                .findById(new ServerMemberId(command.serverId(),currentUser.getId()))
                .orElseThrow(() -> new ServerExceptions(String.format("You don't have access to generate invite code for server id %s", command.serverId())));

        var server = serverMember.getServer();

        if(serverMember.getRole().getName().equals(ServerRoles.USER.name()))
            throw new ServerExceptions(String.format("Only 'Owners' and 'Admins' can generate invite code for server id %s ", command.serverId()));

        var invites = serverInvitesRepository.findByServer(server);

        ServerInvites invite = invites.stream()
                .filter(code -> !code.isExpired() && code.getUses() < code.getMaxUses())
                .findFirst().orElse(null);

        boolean reused = true;

        if(invite==null){
            reused = false;
            var code = generateCode();
            invite = ServerInvites.builder()
                    .code(code)
                    .server(server)
                    .uses(0)
                    .maxUses(250)
                    .expiresAt(LocalDateTime.now().plusDays(7))
                    .build();

            serverInvitesRepository.save(invite);
        }

        springEventPublisher.publish(
                new InviteCodeGeneratedEvent(server.getId(),invite.getCode(),invite.getExpiresAt(),currentUser.getId(),reused)
        );

        return ApiResponse.success(
                reused ? "Reused an existing invite code" : "Generated new invite code",
                new InviteCodeDto(invite.getId(), server.getId(), server.getName(),
                        invite.getCode(), invite.getMaxUses(), invite.getUses(), invite.getExpiresAt())
        );
    }

    private String generateCode(){
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }


}
