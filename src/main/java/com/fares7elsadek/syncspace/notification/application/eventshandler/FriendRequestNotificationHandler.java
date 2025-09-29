package com.fares7elsadek.syncspace.notification.application.eventshandler;

import com.fares7elsadek.syncspace.friendship.domain.events.SendFriendRequestEvent;
import com.fares7elsadek.syncspace.notification.application.mapper.NotificationMapper;
import com.fares7elsadek.syncspace.notification.application.services.NotificationService;
import com.fares7elsadek.syncspace.notification.domain.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.domain.model.Notifications;
import com.fares7elsadek.syncspace.notification.infrastructure.repository.NotificationRepository;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class FriendRequestNotificationHandler {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final UserAccessService userAccessService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handleSendFriendRequest(SendFriendRequestEvent event) {

        try {

            processNotification(event);
            log.debug("Successfully processed SendMessageEvent for messageId: {}", event.getTargetUserId());

        } catch (Exception e) {
            log.error("Failed to process SendMessageEvent for messageId: {} - Error: {}",
                    event.getTargetUserId(), e.getMessage(), e);
            throw new RuntimeException("Notification processing failed", e);
        }

    }

    @Transactional
    protected void processNotification(SendFriendRequestEvent event) {
        var recipient = userAccessService.getUserInfo(event.getTargetUserId());
        var sender = userAccessService.getUserInfo(event.getSenderUserId());
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
                .relatedEntityId(event.getFriendshipId())
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
