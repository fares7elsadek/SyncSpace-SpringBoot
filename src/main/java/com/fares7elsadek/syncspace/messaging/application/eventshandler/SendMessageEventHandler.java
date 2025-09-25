package com.fares7elsadek.syncspace.messaging.application.eventshandler;

import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.application.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.domain.events.SendMessageEvent;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.domain.ws.WebSocketMessageDestinations;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.MessageRepository;
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
public class SendMessageEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleSendMessageEvent(SendMessageEvent event) {
        log.info("Handling SendMessageEvent for channelId={} messageId={}"
                , event.getChannelId(), event.getMessageId());

        Message message = messageRepository.findById(event.getMessageId()).
                orElseThrow(() -> new RuntimeException("Message not found"));

        MessageDto dto = messageMapper.toMessageDto(message);

        if(event.isGroup()){
            // group channel
            String destination = WebSocketMessageDestinations.CHANNEL_MESSAGES.replace("{channelId}",
                    event.getChannelId());
            messagingTemplate.convertAndSend(destination, dto);
            log.info("Group message broadcast to {}", destination);

        }else{
            // private chat
            messagingTemplate.convertAndSendToUser(
                    event.getRecipientId(),
                    WebSocketMessageDestinations.CHANNEL_MESSAGES_PRIVATE,
                    dto
            );

            log.info("Private message sent to user {} at /user/{}/queue/private/messages", event.getRecipientId(), event.getRecipientId());
        }
    }

}
