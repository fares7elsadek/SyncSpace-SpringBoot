package com.fares7elsadek.syncspace.notification.services;

import com.fares7elsadek.syncspace.notification.model.dtos.NotificationDto;

public interface NotificationService {
    void sendRealTimeNotificationPrivate(String userId, NotificationDto notification);
    void sendRealTimeNotificationPublic(String destiniation, NotificationDto notification);
    void sendPushNotification(String userId, String title, String content);
    void markAsRead(String notificationId);
    void markAllAsRead(String userId);
}
