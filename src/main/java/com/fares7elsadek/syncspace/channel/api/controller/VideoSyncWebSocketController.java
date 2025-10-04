package com.fares7elsadek.syncspace.channel.api.controller;

import com.fares7elsadek.syncspace.channel.domain.events.VideoControlEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VideoSyncWebSocketController {
    private final SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/room/{roomId}/sync")
    public void handleSync(@DestinationVariable String roomId, VideoControlEvent event) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }
}
