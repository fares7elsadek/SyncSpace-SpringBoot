package com.fares7elsadek.syncspace.channel.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record RoomStateDto(
        String roomId,
        String videoUrl,
        double currentTimestamp,
        boolean isPlaying,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime lastUpdatedAt,
        Double playbackRate,
        ChannelDto channel,
        ChannelChatUserDto hoster,
        String videoTitle,
        String thumbnail,
        List<RoomViewerDto> viewers
) {
}
