package com.fares7elsadek.syncspace.server.commands.createserver;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateServerCommand(
        @NotBlank(message = "Server name cannot be blank")
        @Size(min = 3, max = 50, message = "Server name must be between 3 and 50 characters")
        String name,
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,
        boolean isPublic
) implements Command {
}
