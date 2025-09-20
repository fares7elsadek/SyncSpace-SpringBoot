package com.fares7elsadek.syncspace.server.api.dtos;

import java.time.LocalDateTime;

public record ServerUserDto(String id, String username, String firstname, String lastname,
                            String email, LocalDateTime lastSeen, LocalDateTime createdAt,boolean online,String avatarUrl) {
}
