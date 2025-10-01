package com.fares7elsadek.syncspace.channel.application.services;

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
    private final RedisTemplate<String, RoomState> redisTemplate;
    private final RoomStateRepository roomStateRepository;
    private final ChannelRepository channelRepository;

    private static final String ROOM_STATE_PREFIX = "room:state:";

    public RoomState getRoomState(String channelId){
        RoomState roomState = redisTemplate.opsForValue().get(ROOM_STATE_PREFIX + channelId);

        if(roomState == null){
            var channel =  channelRepository.findById(channelId)
                    .orElseThrow(() -> new RuntimeException("channel not found"));

            var room = channel.getRoomState();

            roomState = RoomState.builder()
                    .id(room.getId())
                    .videoUrl(room.getVideoUrl())
                    .currentTimestamp(room.getCurrentTimestamp())
                    .isPlaying(room.getIsPlaying())
                    .lastUpdatedAt(room.getLastUpdatedAt())
                    .playbackRate(room.getPlaybackRate())
                    .build();

            saveToRedis(roomState);
        }

        return roomState;
    }

    public void updateRoomState(RoomState state) {
        state.setLastUpdatedAt(LocalDateTime.now());
        saveToRedis(state);
        CompletableFuture.runAsync(() -> saveToDatabase(state));
    }

    private void saveToDatabase(RoomState state) {
        roomStateRepository.findById(state.getId()).ifPresent(room -> {
            room.setVideoUrl(state.getVideoUrl());
            room.setCurrentTimestamp(state.getCurrentTimestamp());
            room.setIsPlaying(state.getIsPlaying());
            room.setLastUpdatedAt(state.getLastUpdatedAt());
            room.setPlaybackRate(state.getPlaybackRate());
            roomStateRepository.save(room);
        });
    }

    private void saveToRedis(RoomState state) {
        redisTemplate.opsForValue().set(
                ROOM_STATE_PREFIX + state.getId(),
                state,
                Duration.ofHours(24) // TTL
        );
    }

}
