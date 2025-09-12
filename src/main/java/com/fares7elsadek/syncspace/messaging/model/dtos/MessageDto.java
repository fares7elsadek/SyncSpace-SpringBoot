package com.fares7elsadek.syncspace.messaging.model.dtos;

import java.util.List;

public record MessageDto(String channelId, String messageId , String content,MessageUserDto sender, List<String> attachmentUrls) {
}
