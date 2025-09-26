package com.fares7elsadek.syncspace.channel.api.controller;

import com.fares7elsadek.syncspace.channel.api.dtos.ChannelPresenceRequest;
import com.fares7elsadek.syncspace.channel.application.services.ChannelPresenceService;
import com.fares7elsadek.syncspace.channel.domain.events.ChannelPresenceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PresenceWebSocketController {
    private final ChannelPresenceService channelPresenceService;

    @MessageMapping("/channel/view/start")
    @SendTo("/topic/presence")
    public ChannelPresenceEvent startViewing(ChannelPresenceRequest channelPresenceRequest, Principal principal) {
        log.info("channel presence request received {} with userId {}", channelPresenceRequest, principal.getName());
        System.out.println("here ===================================== ");
        channelPresenceService.setUserActive(channelPresenceRequest.channelId(), principal.getName());

        return new ChannelPresenceEvent(
                channelPresenceRequest.channelId(),
                principal.getName(),
                true
        );
    }

    @MessageMapping("/channel/view/end")
    @SendTo("/topic/presence")
    public ChannelPresenceEvent stopViewing(ChannelPresenceRequest request, Principal principal) {
        channelPresenceService.setUserInAtive(request.channelId(), principal.getName());
        return new ChannelPresenceEvent(
                request.channelId(),
                principal.getName(),
                false
        );
    }


}
