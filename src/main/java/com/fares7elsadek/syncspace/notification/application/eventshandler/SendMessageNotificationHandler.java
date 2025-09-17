package com.fares7elsadek.syncspace.notification.application.eventshandler;

import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.messaging.domain.events.SendMessageEvent;
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

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SendMessageNotificationHandler {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final UserAccessService userAccessService;
    private final ChannelAccessService channelAccessService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handleSendMessageNotification(SendMessageEvent event) {
        log.info("Processing SendMessageEvent for channelId: {}, messageId: {}, isGroup: {}",
                event.getChannelId(), event.getMessageId(), event.isGroup());

        if (event.isGroup()) {
            processGroupNotification(event);
        } else {
            processPrivateNotification(event);
        }
    }

    private void processPrivateNotification(SendMessageEvent event) {
        User recipient = userAccessService.getUserInfo(event.getRecipientId());

        Notifications notification = createNotificationPrivate(event, recipient);
        Notifications savedNotification = notificationRepository.save(notification);

        notificationService.sendRealTimeNotificationPrivate(
                recipient.getId(),
                notificationMapper.toNotificationDto(savedNotification)
        );

        log.info("Private notification created with ID: {} for recipient: {}",
                savedNotification.getId(), recipient.getId());
    }

    private void processGroupNotification(SendMessageEvent event) {
        List<Notifications> notifications = createNotificationGroup(event);
        List<Notifications> savedNotifications = notificationRepository.saveAll(notifications);

        log.info("Group notifications created for channelId: {}, count: {}",
                event.getChannelId(), savedNotifications.size());
    }

    private Notifications createNotificationPrivate(SendMessageEvent event, User recipient) {
        return Notifications.builder()
                .user(recipient)
                .type(determineNotificationType(event))
                .title(generateNotificationTitle(event))
                .content(generateNotificationContent(event))
                .relatedEntityId(event.getMessageId())
                .read(false)
                .build();
    }

    private List<Notifications> createNotificationGroup(SendMessageEvent event) {
        String title = generateNotificationTitle(event);
        String content = generateNotificationContent(event);
        NotificationType type = determineNotificationType(event);

        var users = channelAccessService.getChannelMembers(event.getChannelId());
        List<Notifications> notifications = new ArrayList<>();

        for (var channelMember : users) {
            Notifications notification = Notifications.builder()
                    .user(channelMember.getUser())
                    .type(type)
                    .title(title)
                    .content(content)
                    .relatedEntityId(event.getMessageId())
                    .read(false)
                    .build();
            notifications.add(notification);
        }
        return notifications;
    }

    private String generateNotificationTitle(SendMessageEvent event) {
        return event.isGroup() ? "New Group Message" : "New Direct Message";
    }

    private String generateNotificationContent(SendMessageEvent event) {
        return event.isGroup()
                ? "You have a new message in the group chat"
                : "You have received a new direct message";
    }

    private NotificationType determineNotificationType(SendMessageEvent event) {
        return event.isGroup() ? NotificationType.GROUP_MESSAGE : NotificationType.DIRECT_MESSAGE;
    }
}
