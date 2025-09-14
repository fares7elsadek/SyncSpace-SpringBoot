package com.fares7elsadek.syncspace.friendship.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RemoveFriendshipEvent extends DomainEvent {
    private final String senderUserId;
    private final String targetUserId;
}
