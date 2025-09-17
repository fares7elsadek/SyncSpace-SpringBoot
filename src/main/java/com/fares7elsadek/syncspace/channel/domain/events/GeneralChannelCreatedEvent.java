package com.fares7elsadek.syncspace.channel.domain.events;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class GeneralChannelCreatedEvent  {
    private final String serverId;
    private final String channelId;
    private final String serverName;
    private final String userId;
}
