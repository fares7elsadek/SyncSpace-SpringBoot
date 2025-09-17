package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;

@Getter
public class ChannelMemberRemovedEvent extends DomainEvent {
    private final String userId;
    private final String channelId;
    private final String serverId;

    public ChannelMemberRemovedEvent(String eventOwnerId, String userId, String channelId, String serverId) {
        super("removeChannelMember", eventOwnerId);
        this.userId = userId;
        this.channelId = channelId;
        this.serverId = serverId;
    }

    public static ChannelMemberRemovedEvent toEvent(String preformerId,String userId,String channelId,String serverId) {
        return new ChannelMemberRemovedEvent(preformerId,userId,channelId,serverId);
    }
}
