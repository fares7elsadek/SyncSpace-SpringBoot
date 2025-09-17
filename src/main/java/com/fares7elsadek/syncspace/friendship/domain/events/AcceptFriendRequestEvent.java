package com.fares7elsadek.syncspace.friendship.domain.events;

import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class AcceptFriendRequestEvent extends DomainEvent {
    private final String senderUserId;
    private final String targetUserId;

    public AcceptFriendRequestEvent(String eventOwnerId, String senderUserId, String targetUserId) {
        super("acceptFriend", eventOwnerId);
        this.senderUserId = senderUserId;
        this.targetUserId = targetUserId;
    }

    public static AcceptFriendRequestEvent toEntity(Friendships friendships) {
        return new AcceptFriendRequestEvent(friendships.getCreatedBy(),friendships.getRequester().getId(),
                friendships.getAddressee().getId());
    }
}
