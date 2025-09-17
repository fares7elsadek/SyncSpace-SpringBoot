package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class DeleteChannelEvent extends DomainEvent {
    private final String channelId;
    private final String serverId;

    public DeleteChannelEvent(String eventOwnerId, String channelId, String serverId) {
        super("deleteChannel", eventOwnerId);
        this.channelId = channelId;
        this.serverId = serverId;
    }

    public static DeleteChannelEvent toEvent(String channelId,String serverId,String userId){
        return new DeleteChannelEvent(userId,channelId,serverId);
    }
}
