package com.fares7elsadek.syncspace.notification.eventshandler;

import com.fares7elsadek.syncspace.friendship.shared.SendFriendRequestEvent;
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
public class FriendRequestNotificationHandler {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final UserValidationService userValidationService;

    @TransactionalEventListener(value = SendFriendRequestEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<Void> handleSendFriendRequest(SendFriendRequestEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {

                processNotification(event);
                log.debug("Successfully processed SendMessageEvent for messageId: {}", event.getTargetUserId());

            } catch (Exception e) {
                log.error("Failed to process SendMessageEvent for messageId: {} - Error: {}",
                        event.getTargetUserId(), e.getMessage(), e);
                throw new RuntimeException("Notification processing failed", e);
            }
        });
    }

    @Transactional
    protected void processNotification(SendFriendRequestEvent event) {
        var recipient = userValidationService.getUserInfo(event.getTargetUserId());
        var sender = userValidationService.getUserInfo(event.getSenderUserId());
        Notifications notification = createNotification(event, recipient,sender);
        var savedNotification = notificationRepository.save(notification);


        notificationService.sendRealTimeNotificationPrivate(recipient.getId(),
                notificationMapper.toNotificationDto(savedNotification));

        log.info("Notification created with ID: {} for recipient: {}",
                savedNotification.getId(), recipient.getId());
    }

    private Notifications createNotification(SendFriendRequestEvent event, User recipient,User sender) {
        String title = generateNotificationTitle(sender);
        String content = generateNotificationContent(sender);
        NotificationType type = determineNotificationType();

        return Notifications.builder()
                .user(recipient)
                .type(type)
                .title(title)
                .content(content)
                .relatedEntityId(event.getSenderUserId())
                .read(false)
                .build();
    }

    private String generateNotificationTitle(User sender) {
        return String.format("%s sent you a friend request", sender.getUsername());
    }

    private String generateNotificationContent(User sender) {
        return String.format("You can accept or decline %s's friend request.", sender.getUsername());
    }

    private NotificationType determineNotificationType() {
        return NotificationType.FRIEND_REQUEST;
    }
}
