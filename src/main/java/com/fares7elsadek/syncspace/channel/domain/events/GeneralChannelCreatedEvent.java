package com.fares7elsadek.syncspace.channel.domain.events;


import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GeneralChannelCreatedEvent extends DomainEvent {
    private final String serverId;
    private final String channelId;
    private final String serverName;
    private final String userId;
}
