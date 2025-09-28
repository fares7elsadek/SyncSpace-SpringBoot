package com.fares7elsadek.syncspace.server.application.commands.addmember;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record AddServerMemberCommand(
        String serverId
        ,String username) implements Command {
}
