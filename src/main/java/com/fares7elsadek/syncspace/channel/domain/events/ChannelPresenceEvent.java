package com.fares7elsadek.syncspace.channel.domain.events;

public record ChannelPresenceEvent(String channelId,
        String userId,
        boolean active) {
}
