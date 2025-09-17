package com.fares7elsadek.syncspace.notification.application.mapper;

import com.fares7elsadek.syncspace.notification.domain.model.Notifications;
import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.api.dtos.UserNotficationDto;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
    public UserNotficationDto toUserDto(User user){
        return new  UserNotficationDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt(),user.isOnline(),user.getAvatarUrl()
        );
    }

    public NotificationDto toNotificationDto(Notifications notification){
        return new  NotificationDto(
                notification.getId(),
                notification.getType().name(),
                notification.getTitle(),
                notification.getContent(),
                notification.getRelatedEntityId()
        );
    }
}
