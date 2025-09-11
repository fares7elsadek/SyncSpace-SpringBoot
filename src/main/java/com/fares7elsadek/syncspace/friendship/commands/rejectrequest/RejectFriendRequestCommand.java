package com.fares7elsadek.syncspace.friendship.commands.rejectrequest;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RejectFriendRequestCommand(
        @NotBlank(message = "Request ID cannot be blank")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Request ID must be a valid UUID")
        String id
) implements Command {
}
