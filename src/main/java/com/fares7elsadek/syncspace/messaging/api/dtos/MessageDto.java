package com.fares7elsadek.syncspace.messaging.api.dtos;

import java.util.List;

public record MessageDto(String channelId, String messageId , String content,MessageUserDto sender, List<String> attachmentUrls
,String sentAt) {
}
