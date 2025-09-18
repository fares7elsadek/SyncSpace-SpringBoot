package com.fares7elsadek.syncspace.config.websockets;

import com.fares7elsadek.syncspace.user.shared.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal user = accessor.getUser();

        if (user == null) {
            log.warn("User is null, headers = {}", accessor.toNativeHeaderMap());
            return;
        }

        String userId = user.getName();
        log.info("WebSocket connection established - Session: {}, User: {}", sessionId, userId);
        presenceService.setOnline(userId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        Principal user = (Principal) accessor.getSessionAttributes().get("user");

        if (user == null) {
            log.warn("User was not authenticated for disconnecting session: {}", sessionId);
            return;
        }

        String userId = user.getName();

        log.info("WebSocket connection disconnected - Session: {}, User: {}", sessionId, userId);
        presenceService.setOffline(userId, sessionId);
    }
}