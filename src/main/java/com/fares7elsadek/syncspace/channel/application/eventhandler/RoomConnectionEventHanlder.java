package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.domain.events.RoomConnectionEvent;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoomConnectionEventHanlder {

    private final ChannelMemberRepository channelMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSendFriendRequest(RoomConnectionEvent event) {
       var members = channelMemberRepository.findByChannelId(event.getChannelId());
       members.forEach(mem ->{
           System.out.println("/topic/user/"+mem.getUser().getId()+"/activity/notify");
           messagingTemplate.convertAndSend(
                   "/topic/user/"+mem.getUser().getId()+"/activity/notify",
                   mem.getUser().getId()
           );
       });
    }
}
