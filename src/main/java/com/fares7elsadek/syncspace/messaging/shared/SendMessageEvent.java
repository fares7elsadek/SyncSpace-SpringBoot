package com.fares7elsadek.syncspace.messaging.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SendMessageEvent extends DomainEvent {
    private final String channelId;
    private final String messageId;
    private final String recipientId;
    private final boolean isGroup;
}
