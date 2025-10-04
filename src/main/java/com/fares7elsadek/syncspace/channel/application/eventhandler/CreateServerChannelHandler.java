package com.fares7elsadek.syncspace.channel.application.eventhandler;

import com.fares7elsadek.syncspace.channel.application.commands.createchannel.CreateChannelCommand;
import com.fares7elsadek.syncspace.channel.domain.enums.ChannelType;
import com.fares7elsadek.syncspace.channel.domain.events.CreateChannelEvent;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.server.domain.events.CreateServerEvent;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateServerChannelHandler {

    private final ChannelRepository channelRepository;
    private final ServerAccessService serverAccessService;
    private final SpringEventPublisher springEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("syncspace-executor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleServerCreationEvent(CreateServerEvent event) {

        var generalChannel =
                createChannel(new CreateChannelCommand(
                        "general",event.getServerId(),
                        "General channel",false,"TEXT"),event);

        var announcementChannel = createChannel(new CreateChannelCommand(
                "announcement",event.getServerId(),
                "Announcement channel",false,"TEXT"),event);

        channelRepository.saveAll(List.of(generalChannel,announcementChannel));

        springEventPublisher
                .publishAsync(CreateChannelEvent
                        .toEvent(generalChannel));

        springEventPublisher
                .publishAsync(CreateChannelEvent
                        .toEvent(announcementChannel));
    }

    private Channel createChannel(CreateChannelCommand command,CreateServerEvent event) {
        log.debug("Creating channel entity");

        String CHANNEL_PREFIX = "#";
        return Channel.builder()
                .name(CHANNEL_PREFIX + command.name().trim())
                .description(command.description() != null ? command.description().trim() : null)
                .isPrivate(command.isPrivate())
                .isGroup(true)
                .channelType(getChannelType(command.type()))
                .server(serverAccessService.getServer(command.serverId()))
                .createdBy(event.getOwnerId())
                .build();

    }

    private ChannelType getChannelType(String type){
        return switch (type) {
            case "STREAMING" -> ChannelType.STREAMING;
            default -> ChannelType.TEXT;
        };
    }

}
