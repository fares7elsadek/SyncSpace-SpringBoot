package com.fares7elsadek.syncspace.messaging.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RemoveAttachmentEvent extends DomainEvent {
    private final String attachmentId;
}
