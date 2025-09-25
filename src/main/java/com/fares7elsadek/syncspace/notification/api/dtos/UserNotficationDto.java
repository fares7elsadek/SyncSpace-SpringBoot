package com.fares7elsadek.syncspace.notification.api.dtos;

public record UserNotficationDto(String id, String username, String firstname, String lastname,
                                 String email, String lastSeen, String createdAt,boolean online,String avatarUrl) {

}
