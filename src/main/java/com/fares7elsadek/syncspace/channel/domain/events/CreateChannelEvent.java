package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class CreateChannelEvent extends DomainEvent {
    private final String channelId;
    private final String serverId;
    private final boolean isGroup;
    private final boolean isPrivate;

    public CreateChannelEvent(String channelId, String serverId, boolean isGroup, boolean isPrivate, String eventOwnerId) {
        super("createChannel",eventOwnerId);
        this.channelId = channelId;
        this.serverId = serverId;
        this.isGroup = isGroup;
        this.isPrivate = isPrivate;
    }

    public static CreateChannelEvent toEvent(Channel channel) {
        return  new CreateChannelEvent(channel.getId()
                ,channel.getServer().getId()
                ,channel.isGroup(),channel.isPrivate()
                ,channel.getCreatedBy());
    }
}
