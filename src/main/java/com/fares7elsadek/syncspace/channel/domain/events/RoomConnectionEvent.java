package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class RoomConnectionEvent extends DomainEvent {
    private final String channelId;

    public RoomConnectionEvent(String eventType, String eventOwnerId,String channelId) {
        super(eventType, eventOwnerId);
        this.channelId = channelId;
    }


}
