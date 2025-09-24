package com.fares7elsadek.syncspace.friendship.shared;

import com.fares7elsadek.syncspace.user.domain.model.User;

import java.util.List;

public interface FriendshipAccessService {
    public List<User> getUserFriends(String userId);
}
