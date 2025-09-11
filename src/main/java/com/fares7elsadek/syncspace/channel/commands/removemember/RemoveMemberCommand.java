package com.fares7elsadek.syncspace.channel.commands.removemember;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RemoveMemberCommand(
        @NotBlank(message = "Channel ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
        String channelId,

        @NotBlank(message = "Server ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Server ID must be a valid UUID")
        String serverId,

        @NotBlank(message = "User ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
        String userId
) implements Command {
}
