package com.fares7elsadek.syncspace.user.api.dto;

public record UserDto (String id, String username, String firstname, String lastname,
                       String email, String lastSeen, String createdAt, String avatarUrl, boolean online){
}
