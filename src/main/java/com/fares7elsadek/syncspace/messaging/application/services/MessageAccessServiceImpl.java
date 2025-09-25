package com.fares7elsadek.syncspace.messaging.application.services;

import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.MessageRepository;
import com.fares7elsadek.syncspace.messaging.shared.MessageAccessService;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageAccessServiceImpl implements MessageAccessService {
    private final MessageRepository messageRepository;
    @Override
    public Message getMessage(String messageId) {
        return messageRepository.findById(messageId).
                orElseThrow(() -> new NotFoundException("Message not found"));
    }

    @Override
    public int getUnreadMessages(String channelId, String lastReadMessageId) {
        return messageRepository.getNumberOfUnreadMessages(channelId, lastReadMessageId);
    }

    @Override
    public Message getLastMessage(String channelId) {
        return messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channelId);
    }
}
