package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.friendship.domain.events.RemoveFriendshipEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveFriendChannelHandler {
    private final ChannelRepository channelRepository;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleRemoveFriendRequest(RemoveFriendshipEvent event) {
        channelRepository.findPrivateChannelByUsers(event.getSenderUserId(), event.getTargetUserId())
                .ifPresent(channelRepository::delete);
    }
}
