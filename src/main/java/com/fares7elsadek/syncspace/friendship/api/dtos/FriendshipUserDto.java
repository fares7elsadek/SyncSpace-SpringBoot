package com.fares7elsadek.syncspace.friendship.api.dtos;

import java.time.LocalDateTime;

public record FriendshipUserDto(String id, String username, String firstName, String lastName,
                                String email, LocalDateTime lastSeen,LocalDateTime createdAt,boolean isOnline,String avatarUrl) {

}
