package com.fares7elsadek.syncspace.notification.commands.markread;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record MarkReadCommand(
        String notificationId
) implements Command {
}
