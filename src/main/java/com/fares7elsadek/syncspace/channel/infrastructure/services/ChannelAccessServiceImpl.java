package com.fares7elsadek.syncspace.channel.infrastructure.services;

import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    @Override
    public List<ChannelMembers> getChannelMembers(String channelId) {
        return channelMemberRepository.findByChannelId(channelId);
    }
}
