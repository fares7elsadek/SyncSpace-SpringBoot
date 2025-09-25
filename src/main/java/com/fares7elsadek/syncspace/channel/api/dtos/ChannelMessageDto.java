package com.fares7elsadek.syncspace.channel.api.dtos;

public record ChannelMessageDto(String channelId, String messageId , String content
        , String sentAt) {
}
