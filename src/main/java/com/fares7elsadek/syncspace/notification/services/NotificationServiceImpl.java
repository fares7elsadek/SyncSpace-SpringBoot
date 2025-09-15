package com.fares7elsadek.syncspace.notification.services;

import com.fares7elsadek.syncspace.notification.model.dtos.NotificationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    @Override
    public void sendRealTimeNotificationPrivate(String userId, NotificationDto notification) {
        try {
            String destination = "/topic/notifications/user/" + userId;
            messagingTemplate.convertAndSend(destination, notification);

            log.debug("Real-time notification sent to user: {} via WebSocket", userId);
        } catch (Exception e) {
            log.error("Failed to send real-time notification to user: {} - Error: {}",
                    userId, e.getMessage(), e);
        }
    }

    @Override
    public void sendRealTimeNotificationPublic(String channelId, NotificationDto notification) {
        try {
            String destination = "/topic/notifications/channel/" + channelId;
            messagingTemplate.convertAndSend(destination, notification);

            log.debug("Real-time notification sent to channel: {} via WebSocket", channelId);
        } catch (Exception e) {
            log.error("Failed to send real-time notification to user: {} - Error: {}",
                    channelId, e.getMessage(), e);
        }
    }

    @Override
    public void sendPushNotification(String userId, String title, String content) {

    }

    @Override
    public void markAsRead(String notificationId) {

    }

    @Override
    public void markAllAsRead(String userId) {

    }
}
