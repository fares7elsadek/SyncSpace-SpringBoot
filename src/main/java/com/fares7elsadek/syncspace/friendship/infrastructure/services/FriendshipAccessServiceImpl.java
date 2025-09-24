package com.fares7elsadek.syncspace.friendship.infrastructure.services;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.shared.FriendshipAccessService;
import com.fares7elsadek.syncspace.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendshipAccessServiceImpl implements FriendshipAccessService {
    private final FriendshipRepository friendshipRepository;
    @Override
    public List<User> getUserFriends(String userId) {
        return friendshipRepository.findFriendshipsByUserId(userId,
                FriendShipStatus.ACCEPTED).stream()
                .map(f -> f.getAddressee()
                        .getId().equals(userId) ?
                        f.getRequester()
                        : f.getAddressee()).toList();
    }
}
