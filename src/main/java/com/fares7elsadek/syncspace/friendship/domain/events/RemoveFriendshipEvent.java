package com.fares7elsadek.syncspace.friendship.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class RemoveFriendshipEvent extends DomainEvent {
    private final String senderUserId;
    private final String targetUserId;

    public RemoveFriendshipEvent(String eventOwnerId, String senderUserId, String targetUserId) {
        super("removeFriend", eventOwnerId);
        this.senderUserId = senderUserId;
        this.targetUserId = targetUserId;
    }

    public static RemoveFriendshipEvent toEvent(String senderUserId, String targetUserId,String  eventOwnerId) {
        return new  RemoveFriendshipEvent(eventOwnerId,senderUserId,targetUserId);
    }

}
