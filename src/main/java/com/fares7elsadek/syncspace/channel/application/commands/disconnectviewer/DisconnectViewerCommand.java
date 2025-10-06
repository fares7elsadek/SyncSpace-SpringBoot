package com.fares7elsadek.syncspace.channel.application.commands.disconnectviewer;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record DisconnectViewerCommand(
        String channelId
) implements Command {
}
