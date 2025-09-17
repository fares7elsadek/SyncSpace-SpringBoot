package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.friendship.shared.AcceptFriendRequestEvent;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AcceptFriendChannelHandler {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserAccessService userAccessService;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional
    public void handleFriendRequestAccept(AcceptFriendRequestEvent event) {
        var user1 = userAccessService.getUserInfo(event.getSenderUserId());
        var user2 = userAccessService.getUserInfo(event.getTargetUserId());

        var channel = createChannel();
        var savedChannel = channelRepository.save(channel);

        var member1 = createChannelMember(savedChannel, user1);
        var member2 = createChannelMember(savedChannel, user2);
        channelMemberRepository.saveAll(List.of(member1, member2));
    }

    private Channel createChannel() {
        return Channel.builder()
                .name("#Private-Chat")
                .description("Private Chat")
                .isPrivate(true)
                .isGroup(false)
                .build();
    }

    private ChannelMembers createChannelMember(Channel channel, User user) {
       return  ChannelMembers.builder()
                .id(new ChannelUserId(channel.getId(), user.getId()))
                .channel(channel)
                .user(user)
                .joinedDate(LocalDateTime.now())
                .build();
    }
}
