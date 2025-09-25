package com.fares7elsadek.syncspace.config.websockets;

import com.fares7elsadek.syncspace.user.shared.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
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

        Authentication authentication = getAuthentication(accessor);

        if (authentication == null) {
            log.warn("No authentication found for session {}, headers = {}", sessionId, accessor.toNativeHeaderMap());
            return;
        }

        String userId = authentication.getName();
        presenceService.setOnline(userId, sessionId);
        log.info("User {} connected with session {}", userId, sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();

        Authentication authentication = getAuthentication(accessor);

        if (authentication == null) {
            log.warn("User was not authenticated for disconnecting session: {}", sessionId);
            return;
        }

        String userId = authentication.getName();
        log.info("WebSocket connection disconnected - Session: {}, User: {}", sessionId, userId);
        presenceService.setOffline(userId, sessionId);
    }

    private Authentication getAuthentication(StompHeaderAccessor accessor) {

        Principal user = accessor.getUser();
        if (user instanceof Authentication) {
            return (Authentication) user;
        }


        if (accessor.getSessionAttributes() != null) {
            Authentication auth = (Authentication) accessor.getSessionAttributes().get("user");
            if (auth != null) {
                return auth;
            }
        }

        return null;
    }
}