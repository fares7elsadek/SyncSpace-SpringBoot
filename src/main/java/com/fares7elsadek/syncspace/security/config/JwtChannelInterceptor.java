package com.fares7elsadek.syncspace.security.config;

import com.fares7elsadek.syncspace.security.utils.KeyCloakAuthenticatoinConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;

@Configuration
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtDecoder jwtDecoder;
    private final KeyCloakAuthenticatoinConverter authConverter;

    public JwtChannelInterceptor(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
        this.authConverter = new KeyCloakAuthenticatoinConverter();
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.debug("Processing CONNECT command for session: {}", accessor.getSessionId());

            List<String> authHeaders = accessor.getNativeHeader("Authorization");

            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);

                if (authHeader.startsWith("Bearer ")) {
                    String bearerToken = authHeader.substring(7);

                    try {
                        Jwt jwt = jwtDecoder.decode(bearerToken);
                        Authentication authentication = authConverter.convert(jwt);

                        if (authentication != null) {
                            accessor.setUser(authentication);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            accessor.getSessionAttributes().put("user", authentication);
                            log.info("WebSocket authentication successful for user: {}", authentication.getName());
                            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
                        } else {
                            log.warn("Authentication conversion failed for session: {}", accessor.getSessionId());
                            return null;
                        }

                    } catch (Exception e) {
                        log.error("JWT decode failed for session: {}", accessor.getSessionId(), e);
                        return null;
                    }
                } else {
                    log.warn("Invalid authorization header format for session: {}", accessor.getSessionId());
                    return null;
                }
            } else {
                log.warn("Missing Authorization header for session: {}", accessor.getSessionId());
                return null;
            }
        }
        return message;
    }
}