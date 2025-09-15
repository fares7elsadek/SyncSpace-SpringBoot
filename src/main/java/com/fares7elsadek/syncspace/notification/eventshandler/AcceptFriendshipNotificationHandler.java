package com.fares7elsadek.syncspace.notification.eventshandler;

import com.fares7elsadek.syncspace.friendship.shared.AcceptFriendRequestEvent;
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
public class AcceptFriendshipNotificationHandler {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final UserValidationService userValidationService;

    @TransactionalEventListener(value = AcceptFriendRequestEvent.class,
            phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public CompletableFuture<Void> handleAcceptFriendship(AcceptFriendRequestEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                processNotification(event);
                log.debug("Successfully processed AcceptFriendshipEvent for senderId: {}", event.getSenderUserId());
            } catch (Exception e) {
                log.error("Failed to process AcceptFriendshipEvent for senderId: {} - Error: {}",
                        event.getSenderUserId(), e.getMessage(), e);
                throw new RuntimeException("Notification processing failed", e);
            }
        });
    }

    @Transactional
    protected void processNotification(AcceptFriendRequestEvent event) {
        User recipient = userValidationService.getUserInfo(event.getSenderUserId());

        Notifications notification = createNotification(event, recipient);
        var savedNotification = notificationRepository.save(notification);

        notificationService.sendRealTimeNotificationPrivate(
                recipient.getId(),
                notificationMapper.toNotificationDto(savedNotification)
        );

        log.info("Notification created with ID: {} for recipient: {}",
                savedNotification.getId(), recipient.getId());
    }

    private Notifications createNotification(AcceptFriendRequestEvent event, User recipient) {
        User accepter = userValidationService.getUserInfo(event.getTargetUserId());

        String title = accepter.getUsername() + " accepted your friend request";
        String content = "You are now friends with " + accepter.getUsername();

        return Notifications.builder()
                .user(recipient)
                .type(NotificationType.FRIEND_ACCEPTED)
                .title(title)
                .content(content)
                .relatedEntityId(accepter.getId())
                .read(false)
                .build();
    }
}
