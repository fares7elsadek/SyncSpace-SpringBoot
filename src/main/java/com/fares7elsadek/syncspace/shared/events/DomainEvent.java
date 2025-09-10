package com.fares7elsadek.syncspace.shared.events;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    private final String id = UUID.randomUUID().toString();
    private final Instant occurredOn = Instant.now();
}
