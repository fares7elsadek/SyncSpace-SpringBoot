package com.fares7elsadek.syncspace.friendship.infrastructure.services;

import com.fares7elsadek.syncspace.friendship.shared.FriendshipAccessService;
import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipAccessServiceImpl implements FriendshipAccessService {
    private final FriendshipRepository friendshipRepository;
    @Override
    public List<String> getUserFriends(String userId) {
        return friendshipRepository.findFriendshipsByUserId(userId,
                FriendShipStatus.ACCEPTED).stream()
                .map(f -> f.getAddressee()
                        .getId().equals(userId) ?
                        f.getRequester().getId()
                        : f.getAddressee().getId()).toList();
    }
}
