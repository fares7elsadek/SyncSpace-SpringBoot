package com.fares7elsadek.syncspace.messaging.application.commands.attachments.removeattachment;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RemoveAttachmentCommand(

        String attachmentId
) implements Command {
}
