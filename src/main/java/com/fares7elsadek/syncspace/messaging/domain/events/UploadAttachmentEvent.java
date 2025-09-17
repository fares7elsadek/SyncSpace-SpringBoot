package com.fares7elsadek.syncspace.messaging.domain.events;

import com.fares7elsadek.syncspace.messaging.domain.model.MessageAttachments;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class UploadAttachmentEvent extends DomainEvent {
    private final String id;
    private final String fileName;
    private final String url;

    public UploadAttachmentEvent(String eventOwnerId, String id, String fileName, String url) {
        super("uploadAttachment", eventOwnerId);
        this.id = id;
        this.fileName = fileName;
        this.url = url;
    }

    public static UploadAttachmentEvent toEvent(MessageAttachments attachments) {
        return  new UploadAttachmentEvent(attachments.getCreatedBy(),attachments.getId(),attachments.getFileName(),attachments.getUrl());
    }
}
