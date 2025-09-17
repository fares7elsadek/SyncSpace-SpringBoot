package com.fares7elsadek.syncspace.notification.eventshandler;

import com.fares7elsadek.syncspace.messaging.shared.DeleteMessageEvent;
import com.fares7elsadek.syncspace.notification.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class RemoveMessageNotificationHandler {
    private final NotificationRepository notificationRepository;

    @EventListener
    @Transactional
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handleRemoveMessageNotification(DeleteMessageEvent event) {

        try {
            log.info("Processing DeleteMessageEvent for messageId: {}, channelId: {}, recipientId: {}, isGroup: {}",
                    event.getMessageId(), event.getChannelId(), event.getRecipientId(), event.isGroup());

            NotificationType type = event.isGroup()
                    ? NotificationType.GROUP_MESSAGE
                    : NotificationType.DIRECT_MESSAGE;

            var notification = notificationRepository
                    .findByRelatedEntityIdAndType(event.getMessageId(), type)
                    .orElseThrow(() -> new RuntimeException("No notification found for related entity id " + event.getMessageId()));

            notificationRepository.delete(notification);

            log.debug("Successfully removed notification for messageId: {}", event.getMessageId());

        } catch (Exception e) {
            log.error("Failed to process DeleteMessageEvent for messageId: {} - Error: {}",
                    event.getMessageId(), e.getMessage(), e);
            throw new RuntimeException("Notification removal failed", e);
        }

    }


}
