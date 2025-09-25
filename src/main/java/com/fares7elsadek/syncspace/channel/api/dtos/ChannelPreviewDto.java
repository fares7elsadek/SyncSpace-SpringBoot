package com.fares7elsadek.syncspace.channel.api.dtos;

public record ChannelPreviewDto(String id, String name, String description, boolean isPrivate, boolean isGroup
        ,ChannelMessageDto lastReadMessage, int unReadMessages) {
}
