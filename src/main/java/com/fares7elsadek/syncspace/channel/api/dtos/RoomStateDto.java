package com.fares7elsadek.syncspace.channel.api.dtos;

import java.time.LocalDateTime;

public record RoomStateDto(
        String roomId,
        String videoUrl,
        double currentTimestamp,
        boolean isPlaying,
        LocalDateTime lastUpdatedAt,
        Double playbackRate,
        ChannelDto channel
) {
}
