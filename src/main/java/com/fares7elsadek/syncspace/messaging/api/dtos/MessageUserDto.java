package com.fares7elsadek.syncspace.messaging.api.dtos;

import java.time.LocalDateTime;

public record MessageUserDto(String id, String username, String firstname, String lastname,
                             String email, LocalDateTime lastSeen, LocalDateTime createdAt,boolean online,String avatarUrl) {
}
