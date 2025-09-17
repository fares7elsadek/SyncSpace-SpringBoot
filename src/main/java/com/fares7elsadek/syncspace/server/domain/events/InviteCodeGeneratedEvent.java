package com.fares7elsadek.syncspace.server.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class InviteCodeGeneratedEvent extends DomainEvent {
    private final String serverId;
    private final String inviteCode;
    private final LocalDateTime expiresAt;
    private final String userId;
    private final boolean reused;

    public InviteCodeGeneratedEvent(String eventOwnerId, String serverId, String inviteCode, LocalDateTime expiresAt, String userId, boolean reused) {
        super("generateInviteCode", eventOwnerId);
        this.serverId = serverId;
        this.inviteCode = inviteCode;
        this.expiresAt = expiresAt;
        this.userId = userId;
        this.reused = reused;
    }

    public static InviteCodeGeneratedEvent toEvent(String ownerId,String serverId, String inviteCode, LocalDateTime expiresAt, String userId, boolean reused){
        return new InviteCodeGeneratedEvent(ownerId,serverId,inviteCode,expiresAt,userId,reused);
    }
}
