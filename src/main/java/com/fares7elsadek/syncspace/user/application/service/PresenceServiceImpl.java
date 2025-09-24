package com.fares7elsadek.syncspace.user.application.service;

import com.fares7elsadek.syncspace.friendship.shared.FriendshipAccessService;
import com.fares7elsadek.syncspace.server.shared.ServerAccessService;
import com.fares7elsadek.syncspace.shared.exceptions.UserNotFoundException;
import com.fares7elsadek.syncspace.user.api.dto.PresenceMessage;
import com.fares7elsadek.syncspace.user.domain.enums.OnlineStatus;
import com.fares7elsadek.syncspace.user.infrastructure.repository.UserRepository;
import com.fares7elsadek.syncspace.user.shared.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceServiceImpl implements PresenceService {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ServerAccessService serverAccessService;
    private final FriendshipAccessService friendshipAccessService;

    @Override
    @Transactional
    public void setOnline(String userId, String sessionId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLastSeen(LocalDateTime.now());
        log.info("Setting user {} online with session: {}", userId, sessionId);
        user.setOnline(true);
        userRepository.save(user);
        broadcastPresence(userId, OnlineStatus.ONLINE, sessionId);
    }

    @Override
    @Transactional
    public void setOffline(String userId, String sessionId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLastSeen(LocalDateTime.now());
        log.info("Setting user {} offline with session: {}", userId, sessionId);
        user.setOnline(false);
        userRepository.save(user);
        broadcastPresence(userId, OnlineStatus.OFFLINE, sessionId);
    }

    public void broadcastPresence(String userId, OnlineStatus status, String sessionId) {
        log.info("=== Broadcasting presence for user: {}, status: {}, session: {} ===", userId, status, sessionId);

        var serverIds = serverAccessService.getUserServers(userId);
        var friends = friendshipAccessService.getUserFriends(userId);

        log.info("Found {} friends and {} servers for user {}", friends.size(), serverIds.size(), userId);

        PresenceMessage presenceMessage = new PresenceMessage(userId, status.name().toUpperCase());


        for (var friend : friends) {
            String destination = "/topic/user/" + friend.getId() + "/presence";
            try {
                messagingTemplate.convertAndSend(destination, presenceMessage);
            } catch (Exception e) {
                log.error("Failed to send presence message to friend {}: {}", friend.getId(), e.getMessage(), e);
            }
        }

        for (String serverId : serverIds) {
            String destination = "/topic/server." + serverId + ".presence";
            try {
                messagingTemplate.convertAndSend(destination, presenceMessage);
            } catch (Exception e) {
                log.error("Failed to send presence message to server {}: {}", serverId, e.getMessage(), e);
            }
        }


    }
}