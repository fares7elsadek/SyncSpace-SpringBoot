package com.fares7elsadek.syncspace.server.eventhandler;

import com.fares7elsadek.syncspace.server.commands.generateinvite.GenerateInviteCodeCommand;
import com.fares7elsadek.syncspace.server.shared.CreateServerEvent;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ServerEventHandler {

    private final CommandBus commandBus;

    @EventListener(CreateServerEvent.class)
    @Async("syncspace-executor")
    public void handleServerCreationEvent(CreateServerEvent createServerEvent) {
        var command = new GenerateInviteCodeCommand(
                createServerEvent.getId()
        );
        commandBus.send(command);
    }
}
