package com.fares7elsadek.syncspace.messaging.shared;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class UploadAttachmentEvent extends DomainEvent {
    private final String id;
    private final String fileName;
    private final String userId;
    private final String url;
}
