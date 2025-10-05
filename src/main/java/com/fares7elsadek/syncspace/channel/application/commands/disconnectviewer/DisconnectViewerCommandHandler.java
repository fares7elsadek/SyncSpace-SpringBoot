package com.fares7elsadek.syncspace.channel.application.commands.disconnectviewer;

import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.domain.events.RoomConnectionEvent;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
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
    private final RoomStateRepository roomStateRepository;
    private final UserAccessService userAccessService;
    private final SimpMessagingTemplate messagingTemplate;
    private final RoomStateService roomStateService;
    private final SpringEventPublisher springEventPublisher;
    @Override
    public ApiResponse<Void> handle(DisconnectViewerCommand command) {
        var room = roomStateRepository.findById(command.roomId())
                .orElseThrow(() -> new RuntimeException("Room Not Found"));

        var currentUser = userAccessService.getCurrentUserInfo();
        var viewers = room.getViewers();
        boolean isExist = viewers.stream().anyMatch(viewer -> viewer.getUser().getId().equals(currentUser.getId()));
        if(isExist) {
            var viewer = viewers
                    .stream()
                    .filter(roomViewer -> roomViewer.getUser()
                            .getId().equals(currentUser.getId())).findFirst();

            if(viewer.isPresent()) {
                roomViewerRepository.delete(viewer.get());
                viewers.remove(viewer.get());
                room.setViewers(viewers);
                roomStateService.updateRoomState(room.getChannel().getId(),room);
            }
        }
        System.out.println("disconnect id is " + command.roomId());
        messagingTemplate.convertAndSend(
                "/topic/room/" + room.getChannel().getId() + "/disconnect",
                currentUser.getId()
        );

        springEventPublisher.publish(new RoomConnectionEvent(
                "RoomConnection"
                ,currentUser.getId(),room.getChannel().getId()));

        return ApiResponse.success("Disconnected successfully",null);
    }
}
