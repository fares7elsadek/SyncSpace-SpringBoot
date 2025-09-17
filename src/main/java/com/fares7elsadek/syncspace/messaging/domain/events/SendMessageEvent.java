package com.fares7elsadek.syncspace.messaging.domain.events;

import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class SendMessageEvent extends DomainEvent {
    private final String channelId;
    private final String messageId;
    private final String recipientId;
    private final boolean isGroup;

    public SendMessageEvent(String eventOwnerId, String channelId, boolean isGroup, String recipientId, String messageId) {
        super("sendMessage", eventOwnerId);
        this.channelId = channelId;
        this.isGroup = isGroup;
        this.recipientId = recipientId;
        this.messageId = messageId;
    }

    public static SendMessageEvent toEvent(Message message,String channelId,boolean isGroup,String recipientId) {
        return new SendMessageEvent(message.getCreatedBy(),channelId,isGroup,recipientId,message.getId());
    }
}
