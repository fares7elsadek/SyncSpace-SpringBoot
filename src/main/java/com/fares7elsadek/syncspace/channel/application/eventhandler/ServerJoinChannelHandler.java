package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.shared.InviteJoinEvent;
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
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServerJoinChannelHandler {

    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserAccessService userAccessService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional
    public void handleServerJoin(InviteJoinEvent event) {
        var channels = channelRepository.findAllPublicServerChannels(event.getServerId());
        var user = userAccessService.getUserInfo(event.getUserId());
        List<ChannelMembers> channelMembers = new ArrayList<>();
        channels.forEach(channel -> {
            var member = createMember(new ChannelUserId(channel.getId(),user.getId()),channel,user);
            channelMembers.add(member);
        });
        channelMemberRepository.saveAll(channelMembers);
    }

    private ChannelMembers createMember(ChannelUserId id, Channel channel, User user){
        return ChannelMembers.builder()
                .id(id)
                .channel(channel)
                .user(user)
                .joinedDate(LocalDateTime.now())
                .build();
    }
}
