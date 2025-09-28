package com.fares7elsadek.syncspace.server.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class AddServerMemberEvent extends DomainEvent {
    private final String userId;
    private final String serverId;

    public AddServerMemberEvent(String eventOwnerId, String userId, String serverId) {
        super("AddServerMember", eventOwnerId);
        this.userId = userId;
        this.serverId = serverId;
    }

    public static AddServerMemberEvent toEvent(String eventOwnerId, String userId, String serverId) {
        return new AddServerMemberEvent(eventOwnerId, userId, serverId);
    }
}
