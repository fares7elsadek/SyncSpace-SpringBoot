package com.fares7elsadek.syncspace.friendship.application.commands.rejectrequest;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record RejectFriendRequestCommand(
        String id
) implements Command {
}
