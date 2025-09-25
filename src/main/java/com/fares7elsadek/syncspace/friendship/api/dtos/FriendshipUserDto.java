package com.fares7elsadek.syncspace.friendship.api.dtos;

public record FriendshipUserDto(String id, String username, String firstName, String lastName,
                                String email, String lastSeen,String createdAt,boolean isOnline,String avatarUrl) {

}
