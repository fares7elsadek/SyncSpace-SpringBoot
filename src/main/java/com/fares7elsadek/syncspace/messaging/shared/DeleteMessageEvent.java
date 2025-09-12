package com.fares7elsadek.syncspace.messaging.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DeleteMessageEvent extends DomainEvent {
    private final String messageId;
}
