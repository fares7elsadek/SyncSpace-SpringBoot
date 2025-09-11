package com.fares7elsadek.syncspace.friendship.commands.sendrequest;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SendFriendRequestCommand(
        @NotBlank(message = "User ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
        String userId
) implements Command {
}
