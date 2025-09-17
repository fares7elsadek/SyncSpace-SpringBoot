package com.fares7elsadek.syncspace.shared.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final String eventId;
    private final Instant occurredOn;
    private final String eventType;
    private final String eventOwnerId;

    protected DomainEvent(String eventType,String eventOwnerId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = Instant.now();
        this.eventType = eventType;
        this.eventOwnerId = eventOwnerId;
    }
}
