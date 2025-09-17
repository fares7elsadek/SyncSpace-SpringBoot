package com.fares7elsadek.syncspace.channel.domain.events;

import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.shared.events.DomainEvent;
import lombok.Getter;


@Getter
public class ChannelMemberAddedEvent extends DomainEvent {
    private final String userId;
    private final String channelId;
    private final String serverId;

    public ChannelMemberAddedEvent(String eventOwnerId, String userId, String channelId, String serverId) {
        super("addChannelMember", eventOwnerId);
        this.userId = userId;
        this.channelId = channelId;
        this.serverId = serverId;
    }

    public static ChannelMemberAddedEvent toEvent(ChannelMembers member) {
        return  new  ChannelMemberAddedEvent(
                member.getCreatedBy(),
                member.getUser().getId(),
                member.getChannel().getId(),
                member.getChannel().getServer().getId()
        );
    }
}
