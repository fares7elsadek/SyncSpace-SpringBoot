package com.fares7elsadek.syncspace.friendship.application.commands.sendrequest;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record SendFriendRequestCommand(
        String username
) implements Command {
}
