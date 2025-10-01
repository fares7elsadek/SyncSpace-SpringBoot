package com.fares7elsadek.syncspace.channel.api.controller;

import com.fares7elsadek.syncspace.channel.domain.events.VideoControlEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class VideoSyncWebSocketController {
    @MessageMapping("/room/{roomId}/sync")
    @SendTo("/topic/room/{roomId}")
    public VideoControlEvent handleSync(VideoControlEvent event) {
        return event;
    }
}
