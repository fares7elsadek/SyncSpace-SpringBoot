package com.fares7elsadek.syncspace.notification.eventshandler;

import com.fares7elsadek.syncspace.notification.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.server.shared.DeleteServerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;
@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteServerNotificationHandler {
    private final NotificationRepository notificationRepository;

    @TransactionalEventListener(value = DeleteServerEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<Void> handleDeleteServer(DeleteServerEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {

                NotificationType type = NotificationType.NEW_SERVER;

                var notification = notificationRepository
                        .findByRelatedEntityIdAndType(event.getServerId(), type)
                        .orElseThrow(() -> new RuntimeException("No notification found for related entity id " + event.getServerId()));

                notificationRepository.delete(notification);

                log.debug("Successfully removed notification for messageId: {}", event.getServerId());

            } catch (Exception e) {
                log.error("Failed to process DeleteMessageEvent for messageId: {} - Error: {}",
                        event.getServerId(), e.getMessage(), e);
                throw new RuntimeException("Notification removal failed", e);
            }
        });
    }
}
