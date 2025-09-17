package com.fares7elsadek.syncspace.server.domain.events;

import com.fares7elsadek.syncspace.server.domain.model.Server;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;



@Getter
public class DeleteServerEvent extends DomainEvent {
    private final String serverId;
    private final String name;
    private final String ownerId;

    public DeleteServerEvent(String eventOwnerId, String serverId, String name, String ownerId) {
        super("deleteServer", eventOwnerId);
        this.serverId = serverId;
        this.name = name;
        this.ownerId = ownerId;
    }

    public static DeleteServerEvent toEvent(Server server) {
        return new  DeleteServerEvent(server.getCreatedBy(),server.getId(),server.getName(),server.getCreatedBy());
    }
}
