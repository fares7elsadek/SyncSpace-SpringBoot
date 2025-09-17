package com.fares7elsadek.syncspace.server.domain.events;

import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class CreateServerEvent extends DomainEvent {
    private final String serverId;
    private final String name;
    private final String ownerId;

    public CreateServerEvent(String eventOwnerId, String serverId, String name, String ownerId) {
        super("createServer", eventOwnerId);
        this.serverId = serverId;
        this.name = name;
        this.ownerId = ownerId;
    }

    public static CreateServerEvent toEvent(Server server) {
        return new  CreateServerEvent(server.getCreatedBy(),server.getId(),server.getName(),server.getCreatedBy());
    }
}
