package com.fares7elsadek.syncspace.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
@EnableWebSocketSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSocketSecurityConfig {
    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
                .nullDestMatcher().authenticated()
                .simpSubscribeDestMatchers("/user/queue/errors").authenticated()
                .simpSubscribeDestMatchers("/user/queue/private/**").authenticated()
                .simpSubscribeDestMatchers("/topic/channel/**").hasRole("USER")
                .simpDestMatchers("/app/chat.send").hasRole("USER")
                .simpDestMatchers("/app/chat.join").hasRole("USER")
                .simpDestMatchers("/app/chat.leave").hasRole("USER")
                .simpDestMatchers("/app/user.typing").hasRole("USER")
                .anyMessage().denyAll();

        return messages.build();
    }
}
