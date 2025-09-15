package com.fares7elsadek.syncspace.notification.mapper;

import com.fares7elsadek.syncspace.notification.model.Notifications;
import com.fares7elsadek.syncspace.notification.model.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.model.dtos.UserNotficationDto;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationMapper {
    public UserNotficationDto toUserDto(User user){
        return new  UserNotficationDto(
                user.getId(),user.getUsername(),
                user.getFirstName(),user.getLastName(),user.getEmail(),
                user.getLastSeen(),user.getCreatedAt()
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
