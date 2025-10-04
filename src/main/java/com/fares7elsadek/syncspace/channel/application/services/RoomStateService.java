package com.fares7elsadek.syncspace.channel.application.services;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.ChannelRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class RoomStateService {
    private final RedisTemplate<String, RoomStateDto> redisTemplate;
    private final RoomStateRepository roomStateRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMapper channelMapper;

    private static final String ROOM_STATE_PREFIX = "room:state:";

    public RoomStateDto getRoomState(String channelId){
        // Try to get from Redis using channelId as key
        RoomStateDto roomState = redisTemplate.opsForValue().get(ROOM_STATE_PREFIX + channelId);

        if(roomState == null){
            var channel = channelRepository.findById(channelId)
                    .orElseThrow(() -> new RuntimeException("Channel not found"));

            var room = channel.getRoomState();

            if (room == null) {
                room = roomStateRepository.save(RoomState.builder()
                        .channel(channel)
                        .videoUrl("")
                        .currentTimestamp(0.0)
                        .isPlaying(false)
                        .playbackRate(1.0)
                        .build());

                channel.setRoomState(room);
                channelRepository.save(channel);
            }

            roomState = channelMapper.toRoomStateDto(room);

            saveToRedis(channelId, roomState);
        }

        return roomState;
    }

    public void updateRoomState(String channelId, RoomState state) {
        state.setLastUpdatedAt(LocalDateTime.now());
        saveToRedis(channelId, channelMapper.toRoomStateDto(state));
        CompletableFuture.runAsync(() -> saveToDatabase(state));
    }

    private void saveToDatabase(RoomState state) {
        roomStateRepository.findById(state.getId()).ifPresent(room -> {
            room.setVideoUrl(state.getVideoUrl());
            room.setCurrentTimestamp(state.getCurrentTimestamp());
            room.setIsPlaying(state.getIsPlaying());
            room.setHostUser(state.getHostUser());
            room.setLastUpdatedAt(state.getLastUpdatedAt());
            room.setPlaybackRate(state.getPlaybackRate());
            roomStateRepository.save(room);
        });
    }

    private void saveToRedis(String channelId, RoomStateDto state) {
        redisTemplate.opsForValue().set(
                ROOM_STATE_PREFIX + channelId,
                state,
                Duration.ofHours(24)
        );
    }
}
