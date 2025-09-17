package com.fares7elsadek.syncspace.channel.eventhandler;

import com.fares7elsadek.syncspace.channel.commands.createchannel.CreateChannelCommand;
import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.CreateChannelEvent;
import com.fares7elsadek.syncspace.channel.shared.GeneralChannelCreatedEvent;
import com.fares7elsadek.syncspace.friendship.shared.AcceptFriendRequestEvent;
import com.fares7elsadek.syncspace.friendship.shared.RemoveFriendshipEvent;
import com.fares7elsadek.syncspace.server.api.ServerAccessService;
import com.fares7elsadek.syncspace.server.shared.CreateServerEvent;
import com.fares7elsadek.syncspace.server.shared.InviteJoinEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChannelEventHandler {

    private final CommandBus commandBus;
    private final SpringEventPublisher springEventPublisher;
    private final ServerAccessService serverAccessService;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserAccessService userAccessService;

    @EventListener
    @Transactional
    public void handleServerCreationEvent(CreateServerEvent createServerEvent) {
        CreateChannelCommand command1 = new CreateChannelCommand(
                "general",
                createServerEvent.getServerId(),
                "General channel",
                false
        );
        CreateChannelCommand command2 = new CreateChannelCommand(
                "announcements",
                createServerEvent.getServerId(),
                "Announcements channel",
                false
        );
        ApiResponse<ChannelDto> response = commandBus.send(command1);
        commandBus.send(command2);
        ChannelDto channel = response.data();

        springEventPublisher.publish(new
                GeneralChannelCreatedEvent(createServerEvent.getId()
                ,channel.id(),createServerEvent.getName()
                , createServerEvent.getOwnerId()));
    }

    @EventListener
    @Transactional
    public void handlePublicChannelCreated(CreateChannelEvent event) {
        if(!event.isGroup() || event.isPrivate() )
            return;

        List<User> usersServer = serverAccessService.getServerMembers(event.getServerId());
        var channel = channelRepository.findById(event.getChannelId()).get();
        List<ChannelMembers> channelMembers = new ArrayList<>();
        for (User user : usersServer) {
            var id = new ChannelUserId(event.getChannelId(),user.getId());
            var member = ChannelMembers.builder()
                    .id(id)
                    .channel(channel)
                    .user(user)
                    .joinedDate(LocalDateTime.now())
                    .build();
            channelMembers.add(member);
        }
        channelMemberRepository.saveAll(channelMembers);
    }

    @EventListener
    @Transactional
    public void handleFriendRequestAccept(AcceptFriendRequestEvent event) {
        var user1 = userAccessService.getUserInfo(event.getSenderUserId());
        var user2 = userAccessService.getUserInfo(event.getTargetUserId());

        var channel = Channel.builder()
                .name("#Private-Chat")
                .description("Private Chat")
                .isPrivate(true)
                .isGroup(false)
                .build();

        var savedChannel = channelRepository.save(channel);

        var member1 = ChannelMembers.builder()
                .id(new ChannelUserId(savedChannel.getId(), user1.getId()))
                .channel(savedChannel)
                .user(user1)
                .joinedDate(LocalDateTime.now())
                .build();

        var member2 = ChannelMembers.builder()
                .id(new ChannelUserId(savedChannel.getId(), user2.getId()))
                .channel(savedChannel)
                .user(user2)
                .joinedDate(LocalDateTime.now())
                .build();

        channelMemberRepository.saveAll(List.of(member1, member2));
    }


    @EventListener
    @Transactional
    public void handleRemoveFriendRequest(RemoveFriendshipEvent event) {
        channelRepository.findPrivateChannelByUsers(event.getSenderUserId(), event.getTargetUserId())
                .ifPresent(channelRepository::delete);
    }

    @EventListener
    @Transactional
    public void handleServerJoin(InviteJoinEvent event) {
        var channels = channelRepository.findAllPublicServerChannels(event.getServerId());
        var user = userAccessService.getUserInfo(event.getUserId());
        List<ChannelMembers> channelMembers = new ArrayList<>();
        channels.forEach(channel -> {
            var member = ChannelMembers.builder()
                    .id(new ChannelUserId(channel.getId(), user.getId()))
                    .channel(channel)
                    .user(user)
                    .joinedDate(LocalDateTime.now())
                    .build();
            channelMembers.add(member);
        });
        channelMemberRepository.saveAll(channelMembers);
    }
}
