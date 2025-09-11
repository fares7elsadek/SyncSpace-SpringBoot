package com.fares7elsadek.syncspace.server.shared;

import com.fares7elsadek.syncspace.server.model.ServerMemberId;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InviteJoinEvent extends DomainEvent {
    private final String userId;
    private final String serverId;
    private final ServerMemberId serverMemberId;
}
