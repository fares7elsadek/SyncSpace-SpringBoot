package com.fares7elsadek.syncspace.channel.model.dtos;

import java.time.LocalDateTime;

public record ChannelChatUserDto(String id, String username, String firstname, String lastname,
                                String email, LocalDateTime lastSeen, LocalDateTime createdAt, boolean isOnline) {
}
