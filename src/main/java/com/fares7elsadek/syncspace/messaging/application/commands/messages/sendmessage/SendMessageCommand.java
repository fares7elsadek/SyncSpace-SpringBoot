package com.fares7elsadek.syncspace.messaging.application.commands.messages.sendmessage;

import com.fares7elsadek.syncspace.messaging.application.validators.ValidMessageType;
import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SendMessageCommand(
        @NotBlank(message = "Message type is required")
        @ValidMessageType
        String messageType,
        @Size(max = 2000, message = "Message content cannot exceed 2000 characters")
        String content,
        @Size(max = 10, message = "You can attach up to 10 files only")
        List<@NotBlank(message = "Attachment ID cannot be blank") String> attachmentsIds,
        @NotBlank(message = "Channel ID is required")
        String channelId
) implements Command {
}
