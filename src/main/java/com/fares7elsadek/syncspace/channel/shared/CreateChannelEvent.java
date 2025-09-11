package com.fares7elsadek.syncspace.channel.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CreateChannelEvent extends DomainEvent {
    private final String channelId;
    private final String serverId;
    private final boolean isGroup;
    private final boolean isPrivate;
}
