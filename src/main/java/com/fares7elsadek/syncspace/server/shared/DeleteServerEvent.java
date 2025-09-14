package com.fares7elsadek.syncspace.server.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public class DeleteServerEvent extends DomainEvent {
    private final String serverId;
    private final String name;
    private final String ownerId;
}
