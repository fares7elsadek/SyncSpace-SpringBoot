package com.fares7elsadek.syncspace.channel.application.commands.addmember;

import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.domain.events.ChannelMemberAddedEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.server.domain.enums.ServerRoles;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.InsufficientPermissionsException;
import com.fares7elsadek.syncspace.shared.exceptions.ServerExceptions;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import com.fares7elsadek.syncspace.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AddMemberCommandHandler
        implements CommandHandler<AddMemberCommand, ApiResponse<AddMemberResponse>> {

    private final UserAccessService userAccessService;
    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelMapper channelMapper;
    @Override
    @Transactional
    public ApiResponse<AddMemberResponse> handle(AddMemberCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();

        validateUserPermissions(command.serverId(),currentUser);


        var channel = channelValidation(command.channelId(), command.serverId());
        var newMemberUser = membershipValidation(command.userId(),command.channelId());

        var newMember = createMember(command,channel,newMemberUser);

        var savedNewMember = channelMemberRepository.save(newMember);

        springEventPublisher.publish(
                ChannelMemberAddedEvent.toEvent(savedNewMember));

        return ApiResponse.success("User added to channel successfully",channelMapper.toAddMemberResponse(
                channel,newMemberUser));
    }

    private void validateUserPermissions(String serverId, User user){
        log.debug("Validating user permissions for user '{}' in server '{}'",
                user.getId(), serverId);

        if (!serverAccessService.isMember(serverId, user.getId())) {
            throw new UnauthorizedException("User must be a member of the server first.");
        }

        if (!serverAccessService.hasRole(serverId, user.getId(),
                ServerRoles.OWNER, ServerRoles.ADMIN)) {
            throw new InsufficientPermissionsException(
                    "Only server owners or administrators can add members to channels.");
        }
    }

    private Channel channelValidation(String channelId,String serverId){
        log.debug("Validating channel existence for channel '{}' in server '{}'",
                channelId, serverId);

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ServerExceptions("Channel not found."));
        if (!channel.getServer().getId().equals(serverId)) {
            throw new ServerExceptions("Channel does not belong to this server.");
        }
        return channel;
    }

    private User membershipValidation(String userId,String channelId){
        User targetUser = userAccessService.getUserInfo(userId);
        var membershipId = new ChannelUserId(channelId, userId);
        if (channelMemberRepository.existsById(membershipId)) {
            throw new ServerExceptions("User is already a member of this channel.");
        }
        return targetUser;
    }

    private ChannelMembers createMember(AddMemberCommand command,Channel channel,User user){
        return ChannelMembers.builder()
                .id(new ChannelUserId(command.channelId(),command.userId()))
                .channel(channel)
                .user(user)
                .joinedDate(LocalDateTime.now())
                .build();
    }
}
