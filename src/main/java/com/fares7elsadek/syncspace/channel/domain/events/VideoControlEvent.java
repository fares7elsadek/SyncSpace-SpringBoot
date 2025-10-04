package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelChatUserDto;

public record VideoControlEvent(
        String roomId,
        String action,
        double timestamp,
        String videoUrl,
        ChannelChatUserDto user
) {
}
