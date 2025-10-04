package com.fares7elsadek.syncspace.channel.application.commands.resetroom;

import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetRoomCommandHandler implements CommandHandler<ResetRoomCommand, ApiResponse<Void>> {
    private final RoomStateService roomStateService;
    private final RoomStateRepository roomStateRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserAccessService userAccessService;
    @Override
    public ApiResponse<Void> handle(ResetRoomCommand command) {
        var roomState = roomStateService.getRoomState(command.roomId());

        if(roomState == null)
            throw  new RuntimeException("Room State Not Found");

        if(roomState.videoUrl().isBlank() || roomState.hoster() == null)
            return ApiResponse.success("Room rested successfully",null);

        var currentUser = userAccessService.getCurrentUserInfo();
        var hoster = roomState.hoster();
        if(!currentUser.getId().equals(hoster.id()))
            throw new UnauthorizedException("You are not allowed to reset the room");

        var state = roomStateRepository.findById(roomState.roomId())
                .orElseThrow(() -> new RuntimeException("Room State Not Found"));

        state.setHostUser(null);
        state.setVideoUrl("");
        state.setIsPlaying(false);
        state.setCurrentTimestamp(0.0);
        roomStateService.updateRoomState(command.roomId(),  state);
        System.out.println("/topic/room/" + roomState.roomId() + "/reset");
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomState.roomId() + "/reset",
                "reset"
        );

        return ApiResponse.success("Room rested successfully",null);
    }
}
