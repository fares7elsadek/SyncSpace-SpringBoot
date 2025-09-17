package com.fares7elsadek.syncspace.friendship.commands.sendrequest;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record SendFriendRequestCommand(
        String userId
) implements Command {
}
