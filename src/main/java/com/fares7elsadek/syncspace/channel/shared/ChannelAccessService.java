package com.fares7elsadek.syncspace.channel.shared;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.user.domain.model.User;

import java.util.List;
import java.util.Set;

public interface ChannelAccessService {
    Channel getChannel(String channelId);
    ChannelMembers getChannelMembers(String channelId, String memberId);
    void updateLastUpdatedTime(Channel channel);
    List<ChannelMembers> getChannelMembers(String channelId);
    void updateChannelReadState(Channel channel, User user, Message lastReadMessage);
    void updateChannelActiveUsersReadState(String channelId, Set<String> userIds, Message lastReadMessage);

}
