package com.fares7elsadek.syncspace.messaging.api.dtos;

public record MessageUserDto(String id, String username, String firstname, String lastname,
                             String email, String lastSeen, String createdAt,boolean online,String avatarUrl) {
}
