package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.domain.events.CreateChannelEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateChannelHandler {

    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserAccessService userAccessService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handlePublicChannelCreated(CreateChannelEvent event) {
        if(!event.isGroup()) // private chat not group
            return;

        var channel = channelRepository.findById(event.getChannelId()).orElseThrow(
                () -> new RuntimeException("channel not found")
        );

        if(event.isPrivate()){
            var user = userAccessService.getUserInfo(event.getEventOwnerId());
            var member = createMember(new ChannelUserId(channel.getId(),user.getId()), channel, user);
            channelMemberRepository.save(member);
        }else{
            List<User> usersServer = serverAccessService.getServerMembers(event.getServerId());
            List<ChannelMembers> channelMembers = new ArrayList<>();
            usersServer.forEach(u -> {
                var id = new ChannelUserId(event.getChannelId(), u.getId());
                var member = createMember(id,channel,u);
                channelMembers.add(member);
            });
            channelMemberRepository.saveAll(channelMembers);
        }

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
