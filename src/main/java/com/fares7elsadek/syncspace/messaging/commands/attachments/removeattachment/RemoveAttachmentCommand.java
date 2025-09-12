package com.fares7elsadek.syncspace.messaging.commands.attachments.removeattachment;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RemoveAttachmentCommand(
        @NotBlank(message = "Attachment ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Attachment ID must be a valid UUID")
        String attachmentId
) implements Command {
}
