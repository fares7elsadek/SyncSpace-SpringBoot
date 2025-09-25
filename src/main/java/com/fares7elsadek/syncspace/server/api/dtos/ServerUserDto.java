package com.fares7elsadek.syncspace.server.api.dtos;

public record ServerUserDto(String id, String username, String firstname, String lastname,
                            String email, String lastSeen, String createdAt,boolean online,String avatarUrl) {
}
