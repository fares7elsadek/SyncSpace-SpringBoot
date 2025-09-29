package com.fares7elsadek.syncspace.notification.application.mapper;

import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.api.dtos.UserNotficationDto;
import com.fares7elsadek.syncspace.notification.domain.model.Notifications;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
    public UserNotficationDto toUserDto(User user){
        PrettyTime p = new PrettyTime();
        String lastSeen = p.format(user.getLastSeen());
        String createdAt = p.format(user.getCreatedAt());
        return new  UserNotficationDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                lastSeen,createdAt,user.isOnline(),user.getAvatarUrl()
        );
    }

    public NotificationDto toNotificationDto(Notifications notification){
        PrettyTime p = new PrettyTime();
        String createdAt = p.format(notification.getCreatedAt());
        return new  NotificationDto(
                notification.getId(),
                notification.getType().name(),
                notification.getTitle(),
                notification.getContent(),
                notification.getRelatedEntityId(),
                createdAt, notification.isRead()
        );
    }
}
