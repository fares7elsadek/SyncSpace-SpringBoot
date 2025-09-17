package com.fares7elsadek.syncspace.server.application.commands.invitejoin;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record InviteJoinCommand(
        @NotBlank(message = "Server ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
        String serverId
        ,
        @NotBlank(message = "Server invite code cannot be blank")
        String code
) implements Command {
}
