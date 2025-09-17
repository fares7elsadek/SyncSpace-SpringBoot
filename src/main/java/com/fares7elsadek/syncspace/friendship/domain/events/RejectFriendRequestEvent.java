package com.fares7elsadek.syncspace.friendship.domain.events;

import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class RejectFriendRequestEvent extends DomainEvent {
    private final String senderUserId;
    private final String targetUserId;

    public RejectFriendRequestEvent(String eventOwnerId, String senderUserId, String targetUserId) {
        super("rejectFriendRequest", eventOwnerId);
        this.senderUserId = senderUserId;
        this.targetUserId = targetUserId;
    }

    public static RejectFriendRequestEvent toEntity(Friendships friendships) {
        return new RejectFriendRequestEvent(friendships.getCreatedBy(),friendships.getRequester().getId(),
                friendships.getAddressee().getId());
    }
}
