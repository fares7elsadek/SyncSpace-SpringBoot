package com.fares7elsadek.syncspace.notification.eventshandler;

import com.fares7elsadek.syncspace.messaging.shared.SendMessageEvent;
import com.fares7elsadek.syncspace.notification.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.mapper.NotificationMapper;
import com.fares7elsadek.syncspace.notification.model.Notifications;
import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.notification.services.NotificationService;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendMessageNotificationHandler {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final UserValidationService userValidationService;
    @TransactionalEventListener(value = SendMessageEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<Void> handleSendMessageNotification(SendMessageEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                log.info("Processing SendMessageEvent for channelId: {}, messageId: {}, recipientId: {}, isGroup: {}",
                        event.getChannelId(), event.getMessageId(), event.getRecipientId(), event.isGroup());

                processNotification(event);

                log.debug("Successfully processed SendMessageEvent for messageId: {}", event.getMessageId());

            } catch (Exception e) {
                log.error("Failed to process SendMessageEvent for messageId: {} - Error: {}",
                        event.getMessageId(), e.getMessage(), e);
                throw new RuntimeException("Notification processing failed", e);
            }
        });
    }

    @Transactional
    protected void processNotification(SendMessageEvent event) {
        var recipient = userValidationService.getUserInfo(event.getRecipientId());
        Notifications notification = createNotification(event, recipient);
        var savedNotification = notificationRepository.save(notification);

        if(event.isGroup())
            notificationService.sendRealTimeNotificationPublic(event.getChannelId(),
                    notificationMapper.toNotificationDto(savedNotification));
        else
            notificationService.sendRealTimeNotificationPrivate(recipient.getId(),
                    notificationMapper.toNotificationDto(savedNotification));

        log.info("Notification created with ID: {} for recipient: {}",
                savedNotification.getId(), recipient.getId());
    }

    private Notifications createNotification(SendMessageEvent event, User recipient) {
        String title = generateNotificationTitle(event);
        String content = generateNotificationContent(event);
        NotificationType type = determineNotificationType(event);

        return Notifications.builder()
                .user(recipient)
                .type(type)
                .title(title)
                .content(content)
                .relatedEntityId(event.getMessageId())
                .read(false)
                .build();
    }

    private String generateNotificationTitle(SendMessageEvent event) {
        if (event.isGroup()) {
            return "New Group Message";
        } else {
            return "New Direct Message";
        }
    }

    private String generateNotificationContent(SendMessageEvent event) {
        if (event.isGroup()) {
            return "You have a new message in the group chat";
        } else {
            return "You have received a new direct message";
        }
    }

    private NotificationType determineNotificationType(SendMessageEvent event) {
        return NotificationType.FRIEND_REQUEST;
    }


}
