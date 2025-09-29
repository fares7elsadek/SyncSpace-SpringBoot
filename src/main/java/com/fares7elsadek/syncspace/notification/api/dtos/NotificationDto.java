package com.fares7elsadek.syncspace.notification.api.dtos;

public record NotificationDto
        (String id,String type,String title,String content,String relatedEntityId,String createdAt
        ,boolean read) {
}
