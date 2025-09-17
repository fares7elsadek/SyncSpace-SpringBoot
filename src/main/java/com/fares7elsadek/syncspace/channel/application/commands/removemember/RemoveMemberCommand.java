package com.fares7elsadek.syncspace.channel.application.commands.removemember;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record RemoveMemberCommand(

        String channelId,


        String serverId,


        String userId
) implements Command {
}
