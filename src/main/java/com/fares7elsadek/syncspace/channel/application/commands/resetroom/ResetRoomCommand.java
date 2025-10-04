package com.fares7elsadek.syncspace.channel.application.commands.resetroom;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record ResetRoomCommand(
        String roomId
) implements Command {
}
