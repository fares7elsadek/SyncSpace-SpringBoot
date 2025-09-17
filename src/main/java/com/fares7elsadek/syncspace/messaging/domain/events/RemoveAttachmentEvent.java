package com.fares7elsadek.syncspace.messaging.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class RemoveAttachmentEvent extends DomainEvent {
    private final String filename;

    public RemoveAttachmentEvent(String eventOwnerId, String filename) {
        super("removeAttachment", eventOwnerId);
        this.filename = filename;
    }

    public static RemoveAttachmentEvent toEvent(String eventOwnerId, String filename) {
        return new RemoveAttachmentEvent(eventOwnerId, filename);
    }
}
