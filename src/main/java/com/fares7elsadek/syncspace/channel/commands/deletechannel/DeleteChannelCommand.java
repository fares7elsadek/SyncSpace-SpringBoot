package com.fares7elsadek.syncspace.channel.commands.deletechannel;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record DeleteChannelCommand(
        String channelId,
        String serverId
) implements Command {
}
