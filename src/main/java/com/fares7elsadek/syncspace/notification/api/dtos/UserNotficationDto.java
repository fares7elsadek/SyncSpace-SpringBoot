package com.fares7elsadek.syncspace.notification.api.dtos;

import java.time.LocalDateTime;

public record UserNotficationDto(String id, String username, String firstname, String lastname,
                                 String email, LocalDateTime lastSeen, LocalDateTime createdAt,boolean online,String avatarUrl) {

}
