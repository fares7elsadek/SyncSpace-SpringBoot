package com.fares7elsadek.syncspace.channel.application.commands.connectviewer;

import com.fares7elsadek.syncspace.channel.application.services.RoomStateService;
import com.fares7elsadek.syncspace.channel.domain.events.RoomConnectionEvent;
import com.fares7elsadek.syncspace.channel.domain.model.RoomViewer;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomStateRepository;
import com.fares7elsadek.syncspace.channel.infrastructure.repository.RoomViewerRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("connectViewerCommandHandler")
@RequiredArgsConstructor
public class ConnectViewerCommandHanlder implements CommandHandler<ConnectViewerCommand, ApiResponse<Void>> {

    private final RoomViewerRepository roomViewerRepository;
    private final RoomStateRepository roomStateRepository;
    private final UserAccessService userAccessService;
    private final RoomStateService roomStateService;
    private final SimpMessagingTemplate messagingTemplate;
    private final SpringEventPublisher springEventPublisher;

    @Override
    public ApiResponse<Void> handle(ConnectViewerCommand command) {
        var currentUser = userAccessService.getCurrentUserInfo();
        var viewer = roomViewerRepository.findByChannelIdAndUserId(command.channelId(), currentUser.getId())
                ;

        if(viewer.isPresent()) {
            return ApiResponse.success("Already exist",null);
        }else{
            var room = roomStateRepository.findByChannelId(command.channelId())
                    .orElseThrow(() -> new NotFoundException("Room Not Found"));
            var newViewer = RoomViewer.builder()
                    .connectedAt(LocalDateTime.now())
                    .roomState(room)
                    .user(currentUser)
                    .build();
            roomViewerRepository.save(newViewer);
            var viewersList = room.getViewers();
            viewersList.add(newViewer);
            room.setViewers(viewersList);
            roomStateService.updateRoomState(command.channelId(),room);

            messagingTemplate.convertAndSend(
                    "/topic/room/" + room.getChannel().getId() + "/connect",
                    currentUser.getId()
            );

            springEventPublisher.publish(new RoomConnectionEvent(
                    "RoomConnection"
                    ,currentUser.getId(),room.getChannel().getId()));

            return ApiResponse.success("Connected successfully",null);
        }

    }
}
