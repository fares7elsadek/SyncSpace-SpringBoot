package com.fares7elsadek.syncspace.channel.eventhandler;

import com.fares7elsadek.syncspace.channel.commands.createchannel.CreateChannelCommand;
import com.fares7elsadek.syncspace.channel.model.dtos.ChannelDto;
import com.fares7elsadek.syncspace.channel.shared.GeneralChannelCreatedEvent;
import com.fares7elsadek.syncspace.server.shared.CreateServerEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChannelEventHandler {

    private final CommandBus commandBus;
    private final SpringEventPublisher springEventPublisher;
    @EventListener(CreateServerEvent.class)
    @Async("syncspace-executor")
    public void handleServerCreationEvent(CreateServerEvent createServerEvent) {
        CreateChannelCommand command1 = new CreateChannelCommand(
                "general",
                createServerEvent.getServerId(),
                "General channel",
                false
        );
        CreateChannelCommand command2 = new CreateChannelCommand(
                "announcements",
                createServerEvent.getServerId(),
                "Announcements channel",
                false
        );
        ApiResponse<ChannelDto> response = commandBus.send(command1);
        commandBus.send(command2);
        ChannelDto channel = response.data();

        springEventPublisher.publish(new
                GeneralChannelCreatedEvent(createServerEvent.getId()
                ,channel.id(),createServerEvent.getName()
                , createServerEvent.getOwnerId()));
    }
}
