package com.fares7elsadek.syncspace.notification.application.eventshandler;

import com.fares7elsadek.syncspace.notification.application.mapper.NotificationMapper;
import com.fares7elsadek.syncspace.notification.application.services.NotificationService;
import com.fares7elsadek.syncspace.notification.domain.enums.NotificationType;
import com.fares7elsadek.syncspace.notification.domain.model.Notifications;
import com.fares7elsadek.syncspace.notification.infrastructure.repository.NotificationRepository;
import com.fares7elsadek.syncspace.server.domain.events.CreateServerEvent;
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
public class CreateServerNotificationHandler {
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
    public void handleSendMessageNotification(CreateServerEvent event) {

        try {
            log.info("Processing CreateServerEvent for serverId: {}, ownerId: {}",
                    event.getServerId(), event.getOwnerId());

            processNotification(event);

            log.debug("Successfully processed CreateServerEvent for serverId: {}",
                    event.getServerId());
        } catch (Exception e) {
            log.error("Failed to process CreateServerEvent for serverId: {} - Error: {}",
                    event.getServerId(), e.getMessage(), e);
            throw new RuntimeException("Notification creation failed", e);
        }

    }

    @Transactional
    protected void processNotification(CreateServerEvent event) {
        log.debug("Fetching recipient info for ownerId: {}", event.getOwnerId());
        var recipient = userAccessService.getUserInfo(event.getOwnerId());

        log.debug("Creating notification entity for serverId: {}", event.getServerId());
        Notifications notification = createNotification(event, recipient);

        var savedNotification = notificationRepository.save(notification);
        log.info("Notification entity persisted with ID: {}", savedNotification.getId());

        notificationService.sendRealTimeNotificationPrivate(
                recipient.getId(),
                notificationMapper.toNotificationDto(savedNotification)
        );

        log.info("Real-time notification sent to recipientId: {}", recipient.getId());
    }

    private Notifications createNotification(CreateServerEvent event, User recipient) {
        String title = generateNotificationTitle();
        String content = generateNotificationContent();
        NotificationType type = determineNotificationType();

        return Notifications.builder()
                .user(recipient)
                .type(type)
                .title(title)
                .content(content)
                .relatedEntityId(event.getServerId())
                .read(false)
                .build();
    }

    private String generateNotificationTitle() {
        return "New server";
    }

    private String generateNotificationContent() {
        return "New server has been created";
    }

    private NotificationType determineNotificationType() {
        return NotificationType.NEW_SERVER;
    }
}
