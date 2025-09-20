package com.fares7elsadek.syncspace.channel.api.dtos;

import java.time.LocalDateTime;

public record ChannelChatUserDto(String id, String username, String firstname, String lastname,
                                String email, LocalDateTime lastSeen, LocalDateTime createdAt, String avatarUrl,boolean online) {
}
