package com.fares7elsadek.syncspace.channel.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChannelMemberAddedEvent extends DomainEvent {
    private final String userId;
    private final String channelId;
    private final String serverId;
}
