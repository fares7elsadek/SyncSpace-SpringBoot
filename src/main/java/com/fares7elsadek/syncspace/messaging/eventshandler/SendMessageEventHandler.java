package com.fares7elsadek.syncspace.messaging.eventshandler;

import com.fares7elsadek.syncspace.messaging.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.model.Message;
import com.fares7elsadek.syncspace.messaging.model.MessageAttachments;
import com.fares7elsadek.syncspace.messaging.model.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.repository.MessageRepository;
import com.fares7elsadek.syncspace.messaging.shared.SendMessageEvent;
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
public class SendMessageEventHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;

    @TransactionalEventListener(
            value = SendMessageEvent.class
            ,phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    public void handleSendMessageEvent(SendMessageEvent event) {
        log.info("Handling SendMessageEvent for channelId={} messageId={}"
                , event.getChannelId(), event.getMessageId());

        Message message = messageRepository.findById(event.getMessageId()).
                orElseThrow(() -> new RuntimeException("Message not found"));

        MessageDto dto = new MessageDto(
                message.getId(),
                event.getChannelId(),
                message.getContent(),
                messageMapper.toDto(message.getSender()),
                message.getAttachments() == null
                        ? java.util.List.of()
                        : message.getAttachments().stream().map(MessageAttachments::getUrl).toList()
        );

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
