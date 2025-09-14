package com.fares7elsadek.syncspace.messaging.eventshandler;

import com.fares7elsadek.syncspace.channel.shared.GeneralChannelCreatedEvent;
import com.fares7elsadek.syncspace.messaging.commands.messages.sendmessage.SendMessageCommand;
import com.fares7elsadek.syncspace.messaging.enums.MessageType;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageEventHandler {
    private final CommandBus commandBus;
    @EventListener(GeneralChannelCreatedEvent.class)
    @Async("syncspace-executor")
    public void handleServerCreationEvent(GeneralChannelCreatedEvent generalChannelCreatedEvent) {
        var command = new SendMessageCommand(
                MessageType.TEXT.name(),
                String.format(" Welcome to %s! :) ",generalChannelCreatedEvent.getServerName()),
                List.of(),
                generalChannelCreatedEvent.getChannelId()
        );
        commandBus.send(command);
    }
}
