package com.fares7elsadek.syncspace.messaging.application.eventshandler;

import com.fares7elsadek.syncspace.channel.domain.events.GeneralChannelCreatedEvent;
import com.fares7elsadek.syncspace.messaging.application.commands.messages.sendmessage.SendMessageCommand;
import com.fares7elsadek.syncspace.messaging.domain.enums.MessageType;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageEventHandler {
    private final CommandBus commandBus;
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
