package com.fares7elsadek.syncspace.friendship.services;

import com.fares7elsadek.syncspace.friendship.api.FriendshipAccessService;
import com.fares7elsadek.syncspace.friendship.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.repository.FriendshipRepository;
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
