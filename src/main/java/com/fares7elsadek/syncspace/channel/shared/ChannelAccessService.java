package com.fares7elsadek.syncspace.channel.shared;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;

import java.util.List;

public interface ChannelAccessService {
    Channel getChannel(String channelId);
    ChannelMembers getChannelMembers(String channelId, String memberId);
    void updateLastUpdatedTime(Channel channel);
    List<ChannelMembers> getChannelMembers(String channelId);
}
