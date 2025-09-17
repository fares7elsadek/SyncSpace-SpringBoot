package com.fares7elsadek.syncspace.channel.commands.addmember;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record AddMemberCommand(
        String channelId,
        String serverId,
        String userId
) implements Command {}

