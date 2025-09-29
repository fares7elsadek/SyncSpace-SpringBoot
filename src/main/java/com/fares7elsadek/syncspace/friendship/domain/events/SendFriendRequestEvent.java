package com.fares7elsadek.syncspace.friendship.domain.events;

import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class SendFriendRequestEvent extends DomainEvent {
    private final String friendshipId;
    private final String senderUserId;
    private final String targetUserId;

    public SendFriendRequestEvent(String eventOwnerId,String friendshipId ,String senderUserId, String targetUserId) {
        super("sendFriendRequest", eventOwnerId);
        this.senderUserId = senderUserId;
        this.targetUserId = targetUserId;
        this.friendshipId = friendshipId;
    }

    public static SendFriendRequestEvent toEvent(Friendships friendships) {
        return new SendFriendRequestEvent(friendships.getCreatedBy(),friendships.getId(),friendships.getRequester().getId(),
                friendships.getAddressee().getId());
    }
}
