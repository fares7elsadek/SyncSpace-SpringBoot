package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.application.services.ChannelPresenceService;
import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.messaging.domain.events.SendMessageEvent;
import com.fares7elsadek.syncspace.messaging.shared.MessageAccessService;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
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
public class SendMessageChannelHandler {
    private final ChannelAccessService channelAccessService;
    private final MessageAccessService messageAccessService;
    private final ChannelPresenceService channelPresenceService;
    private final UserAccessService userAccessService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSendFriendRequest(SendMessageEvent event) {
       var userIds = channelPresenceService.getActiveUsers(event.getChannelId());
       channelAccessService.updateChannelActiveUsersReadState(event.getChannelId(),
               userIds,messageAccessService.getMessage(event.getMessageId()));
       channelAccessService.updateChannelReadState(
               channelAccessService.getChannel(event.getChannelId()),
               userAccessService.getUserInfo(event.getEventOwnerId()),
               messageAccessService.getMessage(event.getMessageId())
       );
    }
}
