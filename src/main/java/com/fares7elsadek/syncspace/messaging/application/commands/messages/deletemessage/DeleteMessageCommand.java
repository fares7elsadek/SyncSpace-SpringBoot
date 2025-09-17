package com.fares7elsadek.syncspace.messaging.application.commands.messages.deletemessage;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;

public record DeleteMessageCommand(
        @NotBlank(message = "Message ID is required")
        String messageId
) implements Command {
}
