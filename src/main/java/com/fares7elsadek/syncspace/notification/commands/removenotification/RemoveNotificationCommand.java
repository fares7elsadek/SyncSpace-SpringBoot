package com.fares7elsadek.syncspace.notification.commands.removenotification;

import com.fares7elsadek.syncspace.shared.cqrs.Command;

public record RemoveNotificationCommand(
        String notificationId
) implements Command {
}
