package com.fares7elsadek.syncspace.channel.commands.createchannel;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateChannelCommand(
        @NotBlank(message = "Channel name is required")
        @Size(min = 3, max = 50, message = "Channel name must be between 3 and 50 characters")
        String name,
        @NotBlank(message = "Server ID is required")
        String serverId,
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,
        boolean isPrivate

) implements Command { }
