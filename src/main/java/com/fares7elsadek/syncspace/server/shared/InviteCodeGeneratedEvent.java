package com.fares7elsadek.syncspace.server.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class InviteCodeGeneratedEvent extends DomainEvent {
    private final String serverId;
    private final String inviteCode;
    private final LocalDateTime expiresAt;
    private final String userId;
    private final boolean reused;
}
