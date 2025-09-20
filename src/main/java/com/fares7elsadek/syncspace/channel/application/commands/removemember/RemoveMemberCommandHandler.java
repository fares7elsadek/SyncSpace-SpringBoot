package com.fares7elsadek.syncspace.channel.application.commands.removemember;

import com.fares7elsadek.syncspace.channel.domain.events.ChannelMemberRemovedEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.InsufficientPermissionsException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveMemberCommandHandler
        implements CommandHandler<RemoveMemberCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelMemberRepository channelMemberRepository;
    @Override
    @Transactional
    public ApiResponse<String> handle(RemoveMemberCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();

        if (currentUser.getId().equals(command.userId())) {
            throw new UnauthorizedException("You cannot remove yourself from the channel.");
        }

        validateUserPermissions(command.serverId(), currentUser);
        var channel = validateChannelExistence(command);

        User targetUser = userAccessService.getUserInfo(command.userId());

        var membershipId = new ChannelUserId(channel.getId(), targetUser.getId());

        var channelMember = channelMemberRepository.findById(membershipId)
                .orElseThrow(() -> new NotFoundException("Member not found."));


        channelMemberRepository.delete(channelMember);

        springEventPublisher.publish(
                ChannelMemberRemovedEvent.toEvent(currentUser.getId(),targetUser.getId(),command.channelId(),command.serverId()));

        return ApiResponse.success("User removed from channel successfully", targetUser.getId());
    }

    private void validateUserPermissions(String serverId, User user){
        log.debug("Validating user permissions for user '{}' in server '{}'",
                user.getId(), serverId);

        if (!serverAccessService.isMember(serverId, user.getId())) {
            throw new UnauthorizedException("Target user is not part of this server.");
        }

        if (!serverAccessService.hasRole(serverId, user.getId(),
                ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new InsufficientPermissionsException(
                    "Only server owners or administrators can remove members from channels.");
        }
    }

    private Channel validateChannelExistence(RemoveMemberCommand command){
        var channel = channelRepository.findById(command.channelId())
                .orElseThrow(() -> new NotFoundException("Channel not found."));

        if (!channel.getServer().getId().equals(command.serverId())) {
            throw new NotFoundException("Channel does not belong to this server.");
        }
        return channel;
    }

}
