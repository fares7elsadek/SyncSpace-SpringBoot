package com.fares7elsadek.syncspace.channel.services;

import com.fares7elsadek.syncspace.channel.api.ChannelAccessService;
import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.channel.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.repository.ChannelRepository;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChannelAccessServiceImpl implements ChannelAccessService {
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    @Override
    public Channel getChannel(String channelId) {
        return channelRepository.findById(channelId).orElseThrow(
                () -> new NotFoundException(String.format("Channel with id %s not found", channelId))
        );
    }

    @Override
    public ChannelMembers getChannelMembers(String channelId, String memberId) {
        var id = new ChannelUserId(channelId, memberId);
        return channelMemberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Member with id %s not found", memberId)));
    }

    @Override
    public void updateLastUpdatedTime(Channel channel) {
        channel.setUpdatedAt(LocalDateTime.now());
        channelRepository.save(channel);
    }
}
