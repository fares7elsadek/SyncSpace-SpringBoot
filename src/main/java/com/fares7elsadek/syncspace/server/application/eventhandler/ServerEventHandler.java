package com.fares7elsadek.syncspace.server.application.eventhandler;

import com.fares7elsadek.syncspace.server.application.commands.generateinvite.GenerateInviteCodeCommand;
import com.fares7elsadek.syncspace.server.domain.events.CreateServerEvent;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ServerEventHandler {

    private final CommandBus commandBus;

    @EventListener
    @Transactional
    public void handleServerCreationEvent(CreateServerEvent createServerEvent) {

        var command = new GenerateInviteCodeCommand(
                createServerEvent.getServerId()
        );
        commandBus.send(command);

    }
}
