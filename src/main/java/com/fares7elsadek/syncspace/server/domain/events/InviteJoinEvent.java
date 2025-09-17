package com.fares7elsadek.syncspace.server.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class InviteJoinEvent extends DomainEvent {
    private final String userId;
    private final String serverId;

    public InviteJoinEvent(String eventOwnerId, String userId, String serverId) {
        super("joinViaInviteCode", eventOwnerId);
        this.userId = userId;
        this.serverId = serverId;
    }

    public static InviteJoinEvent toEvent(String ownerId,String userId,String serverId){
        return  new InviteJoinEvent(ownerId,userId,serverId);
    }
}
