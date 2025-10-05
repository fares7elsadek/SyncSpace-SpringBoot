package com.fares7elsadek.syncspace.channel.api.dtos;

import java.time.LocalDateTime;

public record RoomViewerDto(
        String id,
        ChannelChatUserDto user,
        LocalDateTime connectedAt
) {
}
