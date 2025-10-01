package com.fares7elsadek.syncspace.channel.domain.events;

public record VideoControlEvent(
        String roomId,
        String action,
        double timestamp,
        String videoUrl,
        String userId
) {
}
