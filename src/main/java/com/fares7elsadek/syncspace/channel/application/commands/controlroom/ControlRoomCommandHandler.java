package com.fares7elsadek.syncspace.channel.application.commands.controlroom;

import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.domain.model.RoomState;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class ControlRoomCommandHandler implements CommandHandler<ControlRoomCommand, ApiResponse<Void>> {
    private final RoomStateService roomStateService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public ApiResponse<Void> handle(ControlRoomCommand command) {
        RoomState currentState = roomStateService.getRoomState(command.channelId());
        Double actualTimestamp = calculateActualTimestamp(currentState);

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
                break;
        }

        roomStateService.updateRoomState(currentState);

        messagingTemplate.convertAndSend(
                "/topic/room/" + command.channelId(),
                command
        );

        return ApiResponse.success("Room state updated",null);
    }

    private Double calculateActualTimestamp(RoomState state) {
        if (!state.getIsPlaying()) {
            return state.getCurrentTimestamp();
        }

        Duration elapsed = Duration.between(state.getLastUpdatedAt(), Instant.now());
        double playbackRate = state.getPlaybackRate() != null ? state.getPlaybackRate() : 1.0;

        return state.getCurrentTimestamp() + (elapsed.toMillis() / 1000.0 * playbackRate);
    }
}
