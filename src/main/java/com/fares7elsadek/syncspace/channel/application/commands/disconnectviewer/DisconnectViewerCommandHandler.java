package com.fares7elsadek.syncspace.channel.application.commands.disconnectviewer;

import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.domain.events.RoomConnectionEvent;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomViewerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DisconnectViewerCommandHandler implements CommandHandler<DisconnectViewerCommand, ApiResponse<Void>> {
    private final RoomViewerRepository roomViewerRepository;
    private final UserAccessService userAccessService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomStateService roomStateService;
    private final SpringEventPublisher springEventPublisher;
    @Override
    public ApiResponse<Void> handle(DisconnectViewerCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();
        var viewer = roomViewerRepository.findByChannelIdAndUserId(command.channelId(), currentUser.getId());

        if(viewer.isPresent()){
            var room = viewer.get().getRoomState();
            roomViewerRepository.delete(viewer.get());
            var viewers = room.getViewers();
            viewers.remove(viewer.get());
            room.setViewers(viewers);
            roomStateService.updateRoomState(command.channelId(), room);

            messagingTemplate.convertAndSend(
                    "/topic/room/" + room.getChannel().getId() + "/disconnect",
                    currentUser.getId()
            );

            springEventPublisher.publish(new RoomConnectionEvent(
                    "RoomConnection"
                    ,currentUser.getId(),room.getChannel().getId()));
        }

        return ApiResponse.success("Disconnected successfully",null);
    }
}
