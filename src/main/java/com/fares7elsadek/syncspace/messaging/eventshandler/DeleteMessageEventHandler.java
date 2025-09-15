package com.fares7elsadek.syncspace.messaging.eventshandler;

import com.fares7elsadek.syncspace.messaging.shared.DeleteMessageEvent;
import com.fares7elsadek.syncspace.messaging.ws.WebSocketMessageDestinations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteMessageEventHandler {

    private final SimpMessagingTemplate messagingTemplate;

    @TransactionalEventListener(value = DeleteMessageEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    public void handleDeleteMessageEvent(DeleteMessageEvent event) {
        log.info("Handling DeleteMessageEvent for channelId={} messageId={}", event.getChannelId(), event.getMessageId());

        if(event.isGroup()){
            // group channel
            String destination = WebSocketMessageDestinations.CHANNEL_DELETIONS.replace("{channelId}",
                    event.getChannelId());
            messagingTemplate.convertAndSend(destination, event.getMessageId());
            log.info("Group message deleted broadcast to {}", destination);

        }else{
            // private chat
            messagingTemplate.convertAndSendToUser(
                    event.getRecipientId(),
                    WebSocketMessageDestinations.CHANNEL_DELETIONS_PRIVATE,
                    event.getMessageId()
            );

            log.info("Private message deleted to user {} at /user/{}/queue/private/deletions", event.getRecipientId(), event.getRecipientId());
        }
    }
}
