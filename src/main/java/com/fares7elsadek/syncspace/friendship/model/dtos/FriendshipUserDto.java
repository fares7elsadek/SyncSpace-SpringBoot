package com.fares7elsadek.syncspace.friendship.model.dtos;

import java.time.LocalDateTime;

public record FriendshipUserDto(String id, String username, String firstname, String lastname,
                                String email, LocalDateTime lastSeen,LocalDateTime createdAt,boolean isOnline) {

}
