package com.fares7elsadek.syncspace.channel.commands.removemember;

import com.fares7elsadek.syncspace.channel.commands.addmember.AddMemberCommand;
import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.ChannelMemberRemovedEvent;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveMemberCommandHandler
        implements CommandHandler<AddMemberCommand, ApiResponse<String>> {

    private final UserValidationService userValidationService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelMemberRepository channelMemberRepository;
    @Override
    public ApiResponse<String> handle(AddMemberCommand command) {
        var currentUser = userValidationService.getCurrentUserInfo();

        if (!serverAccessService.isMember(command.serverId(), currentUser.getId())) {
            throw new ServerExceptions("You are not a member of this server.");
        }

        if (!serverAccessService.hasRole(command.serverId(), currentUser.getId(),
                ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new ServerExceptions("Only Owners or Admins can remove members from channels.");
        }


        Channel channel = channelRepository.findById(command.channelId())
                .orElseThrow(() -> new ServerExceptions("Channel not found. "));

        if (!channel.getServer().getId().equals(command.serverId())) {
            throw new ServerExceptions("Channel does not belong to this server.");
        }

        User targetUser = userValidationService.getUserInfo(command.userId());

        var membershipId = new ChannelUserId(channel.getId(), targetUser.getId());

        var channelMember = channelMemberRepository.findById(membershipId)
                .orElseThrow(() -> new ServerExceptions("Member not found."));


        channelMemberRepository.delete(channelMember);

        springEventPublisher.publish(
                new ChannelMemberRemovedEvent(targetUser.getId()
                        , channel.getId(),command.serverId()));

        return ApiResponse.success("User removed from channel successfully", targetUser.getId());
    }
}
