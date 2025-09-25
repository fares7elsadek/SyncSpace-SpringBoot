package com.fares7elsadek.syncspace.messaging.shared;

import com.fares7elsadek.syncspace.messaging.domain.model.Message;

public interface MessageAccessService {
    Message getMessage(String messageId);
    int getUnreadMessages(String channelId, String lastReadMessageId);
    Message getLastMessage(String channelId);
}
