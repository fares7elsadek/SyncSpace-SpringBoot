package com.fares7elsadek.syncspace.channel.api.dtos;

public record ChannelChatUserDto(String id, String username, String firstname, String lastname,
                                String email, String lastSeen, String createdAt, String avatarUrl,boolean isOnline) {
}
