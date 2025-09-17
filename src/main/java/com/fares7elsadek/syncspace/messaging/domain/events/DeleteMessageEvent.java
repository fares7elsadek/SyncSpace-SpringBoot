package com.fares7elsadek.syncspace.messaging.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class DeleteMessageEvent extends DomainEvent {
    private final String messageId;
    private final String channelId;
    private final String recipientId;
    private final boolean isGroup;

    public DeleteMessageEvent(String eventOwnerId, String messageId, String channelId, String recipientId, boolean isGroup) {
        super("deleteMessage", eventOwnerId);
        this.messageId = messageId;
        this.channelId = channelId;
        this.recipientId = recipientId;
        this.isGroup = isGroup;
    }

    public static DeleteMessageEvent toEvent(String ownerId,String messageId ,String channelId, boolean isGroup, String recipientId) {
        return new DeleteMessageEvent(ownerId,messageId,channelId,recipientId,isGroup);
    }
}
