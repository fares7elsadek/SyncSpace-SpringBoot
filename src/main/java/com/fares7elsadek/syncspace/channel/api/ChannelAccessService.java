package com.fares7elsadek.syncspace.channel.api;

import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.ChannelMembers;

public interface ChannelAccessService {
    Channel getChannel(String channelId);
    ChannelMembers getChannelMembers(String channelId, String memberId);
    void updateLastUpdatedTime(Channel channel);
}
