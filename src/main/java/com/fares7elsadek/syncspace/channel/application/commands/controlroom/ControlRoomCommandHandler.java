package com.fares7elsadek.syncspace.channel.application.commands.controlroom;

import com.fares7elsadek.syncspace.channel.application.mapper.ChannelMapper;
import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.domain.events.VideoControlEvent;
import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class ControlRoomCommandHandler implements CommandHandler<ControlRoomCommand, ApiResponse<Void>> {
    private final RoomStateService roomStateService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomStateRepository roomStateRepository;
    private final UserAccessService userAccessService;
    private final ChannelMapper channelMapper;

    @Override
    public ApiResponse<Void> handle(ControlRoomCommand command) {
        var currentState = roomStateRepository.findById(roomStateService.getRoomState(command.channelId()).roomId()).orElseThrow(
                () -> new RuntimeException("Room state not found")
        );
        Double actualTimestamp = calculateActualTimestamp(currentState);
        var hoster = userAccessService.getUserInfo(command.userId());

        switch (command.action()) {
            case "PLAY":
                currentState.setIsPlaying(true);
                currentState.setCurrentTimestamp(command.timestamp() != null
                        ? command.timestamp()
                        : actualTimestamp);
                break;

            case "PAUSE":
                currentState.setIsPlaying(false);
                currentState.setCurrentTimestamp(actualTimestamp);
                break;

            case "SEEK":
                currentState.setCurrentTimestamp(command.timestamp());
                break;

            case "CHANGE_VIDEO":
                currentState.setVideoUrl(command.videoUrl());
                currentState.setCurrentTimestamp(0.0);
                currentState.setIsPlaying(false);
                currentState.setHostUser(hoster);
                break;
        }

        roomStateService.updateRoomState(command.channelId(),currentState);
        System.out.println("/topic/room/" + command.channelId());
        messagingTemplate.convertAndSend(
                "/topic/room/" + currentState.getId(),
                new VideoControlEvent(command.channelId(), command.action()
                , command.timestamp()!=null ? command.timestamp() : 0.0 , command.videoUrl(),channelMapper.toChannelChatUserDto(hoster))
        );

        return ApiResponse.success("Room state updated",null);
    }

    private Double calculateActualTimestamp(RoomState state) {
        if (!state.getIsPlaying()) {
            return state.getCurrentTimestamp();
        }

        Instant lastUpdatedInstant = state.getLastUpdatedAt().atZone(ZoneOffset.UTC).toInstant();
        Duration elapsed = Duration.between(lastUpdatedInstant, Instant.now());
        double playbackRate = state.getPlaybackRate() != null ? state.getPlaybackRate() : 1.0;

        return state.getCurrentTimestamp() + (elapsed.toMillis() / 1000.0 * playbackRate);
    }
}
