package com.fares7elsadek.syncspace.channel.model.dtos;

public record ChannelChatDto(String id,String name, String description
        , boolean isPrivate, boolean isGroup,ChannelChatUserDto user) {
}
