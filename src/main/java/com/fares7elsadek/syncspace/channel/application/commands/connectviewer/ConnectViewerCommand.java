package com.fares7elsadek.syncspace.channel.application.commands.connectviewer;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record ConnectViewerCommand(
        String channelId
) implements Command {
}
