package com.fares7elsadek.syncspace.friendship.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AcceptFriendRequestEvent extends DomainEvent {
    private final String senderUserId;
    private final String targetUserId;
}
