package com.fares7elsadek.syncspace.channel.commands.addmember;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.ChannelMemberAddedEvent;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AddMemberCommandHandler
        implements CommandHandler<AddMemberCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelMemberRepository channelMemberRepository;
    @Override
    public ApiResponse<String> handle(AddMemberCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();

        if (!serverAccessService.isMember(command.serverId(), currentUser.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        if (!serverAccessService.hasRole(command.serverId(), currentUser.getId(),
                ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new ServerExceptions("Only Owners or Admins can add members to channels.");
        }


        Channel channel = channelRepository.findById(command.channelId())
                .orElseThrow(() -> new ServerExceptions("Channel not found."));
        if (!channel.getServer().getId().equals(command.serverId())) {
            throw new ServerExceptions("Channel does not belong to this server.");
        }

        User targetUser = userAccessService.getUserInfo(command.userId());

        var membershipId = new ChannelUserId(channel.getId(), targetUser.getId());
        if (channelMemberRepository.existsById(membershipId)) {
            throw new ServerExceptions("User is already a member of this channel.");
        }


        ChannelMembers membership = ChannelMembers.builder()
                .id(membershipId)
                .channel(channel)
                .user(targetUser)
                .joinedDate(LocalDateTime.now())
                .build();

        channelMemberRepository.save(membership);

        springEventPublisher.publish(
                new ChannelMemberAddedEvent(targetUser.getId()
                        , channel.getId(),command.serverId()));

        return ApiResponse.success("User added to channel successfully", targetUser.getId());
    }
}
