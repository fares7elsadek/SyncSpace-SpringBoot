package com.fares7elsadek.syncspace.channel.infrastructure.services;

import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelMembers;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelReadState;
import com.fares7elsadek.syncspace.channel.domain.model.ChannelUserId;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelMemberRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelReadStateRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChannelAccessServiceImpl implements ChannelAccessService {
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final ChannelReadStateRepository channelReadStateRepository;
    private final UserAccessService userAccessService;
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

    @Override
    public void updateChannelReadState(Channel channel, User user, Message lastReadMessage) {
        var id = new ChannelUserId(channel.getId(),user.getId());
        var readState = channelReadStateRepository.findById(id);
        if(readState.isPresent()) {
            var state = readState.get();
            state.setLastReadMessage(lastReadMessage);
            channelReadStateRepository.save(state);
        }else{
            var newState = ChannelReadState.builder()
                    .id(id)
                    .user(user)
                    .channel(channel)
                    .lastReadMessage(lastReadMessage)
                    .build();
            channelReadStateRepository.save(newState);
        }
    }
    @Override
    public void updateChannelActiveUsersReadState(String channelId, Set<String> userIds, Message lastReadMessage) {
        var channel = channelRepository.findById(channelId).orElseThrow(() -> new NotFoundException("Channel not found"));
        List<ChannelReadState> channelReadStates = new ArrayList<>();
        userIds.forEach(userId -> {
            var user = userAccessService.getUserInfo(userId);
            var id = new ChannelUserId(channel.getId(),user.getId());

            var readState = channelReadStateRepository.findById(id);
            if(readState.isPresent()) {
                var state = readState.get();
                state.setLastReadMessage(lastReadMessage);
                channelReadStates.add(state);
            }else{
                var newState = ChannelReadState.builder()
                        .id(id)
                        .user(user)
                        .channel(channel)
                        .lastReadMessage(lastReadMessage)
                        .build();
                channelReadStates.add(newState);
            }
        });
        channelReadStateRepository.saveAll(channelReadStates);
    }
}
