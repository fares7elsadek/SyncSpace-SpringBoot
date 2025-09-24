package com.fares7elsadek.syncspace.user.api.dto;

import java.time.LocalDateTime;

public record UserDto (String id, String username, String firstname, String lastname,
                       String email, LocalDateTime lastSeen, LocalDateTime createdAt, String avatarUrl, boolean online){
}
