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
import org.springframework.scheduling.annotation.Async;
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
    private final FriendshipAccessService  friendshipAccessService;

    @Override
    @Transactional
    @Async
    public void setOnline(String userId, String sessionId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLastSeen(LocalDateTime.now());
        log.info("Online session: {}", sessionId);
        user.setOnline(true);
        userRepository.save(user);
        broadcastPresence(userId,OnlineStatus.ONLINE);
    }

    @Override
    @Transactional
    @Async
    public void setOffline(String userId, String sessionId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        user.setLastSeen(LocalDateTime.now());
        log.info("Offline session ID: {}", sessionId);
        user.setOnline(false);
        userRepository.save(user);
        broadcastPresence(userId,OnlineStatus.OFFLINE);
    }

    public void broadcastPresence(String userId, OnlineStatus status) {
        var serverIds = serverAccessService.getUserServers(userId);
        var userIds = friendshipAccessService.getUserFriends(userId);

        for (String friendId : userIds) {
            messagingTemplate.convertAndSendToUser(
                    friendId,
                    "/queue/presence",
                    new PresenceMessage(userId, status.name().toUpperCase())
            );
        }

        for (String serverId : serverIds) {
            messagingTemplate.convertAndSend(
                    "/topic/server." + serverId + ".presence",
                    new PresenceMessage(userId, status.name().toUpperCase())
            );
        }
    }
}
