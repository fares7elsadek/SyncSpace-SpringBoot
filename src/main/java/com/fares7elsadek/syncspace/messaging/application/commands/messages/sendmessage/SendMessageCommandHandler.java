package com.fares7elsadek.syncspace.messaging.application.commands.messages.sendmessage;

import com.fares7elsadek.syncspace.channel.shared.ChannelAccessService;
import com.fares7elsadek.syncspace.channel.domain.model.Channel;
import com.fares7elsadek.syncspace.messaging.domain.enums.MessageType;
import com.fares7elsadek.syncspace.messaging.application.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.domain.model.Message;
import com.fares7elsadek.syncspace.messaging.domain.model.MessageAttachments;
import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.AttachmentRepository;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.MessageRepository;
import com.fares7elsadek.syncspace.messaging.domain.events.SendMessageEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SendMessageCommandHandler implements CommandHandler<SendMessageCommand, ApiResponse<MessageDto>> {

    private final UserAccessService userAccessService;
    private final MessageRepository  messageRepository;
    private final SpringEventPublisher springEventPublisher;
    private final ChannelAccessService channelAccessService;
    private final AttachmentRepository attachmentRepository;
    private final MessageMapper messageMapper;
    @Override
    @Transactional
    public ApiResponse<MessageDto> handle(SendMessageCommand command) {
        var sender = userAccessService.getCurrentUserInfo();
        var channel = channelAccessService.getChannel(command.channelId());

        var message = Message.builder()
                .channel(channel)
                .sender(sender)
                .messageType(getMessageType(command.messageType()))
                .content(command.content())
                .build();

        if (command.attachmentsIds() != null && !command.attachmentsIds().isEmpty()) {
            List<MessageAttachments> attachments = attachmentRepository.findAllById(command.attachmentsIds());

            attachments.forEach(att -> {
                if (!att.getCreatedBy().equals(sender.getId())) {
                    throw new IllegalArgumentException("You cannot attach files you didn’t upload.");
                }
                att.setMessage(message);
            });

            message.setAttachments(attachments);
        }

        String recipientId = "";
        if(!channel.isGroup()){
            recipientId = getRecipientId(channel,sender.getId());
        }

        var savedMessage = messageRepository.save(message);
        channelAccessService.updateLastUpdatedTime(channel);

        springEventPublisher
                .publish(SendMessageEvent
                        .toEvent(savedMessage
                                ,channel.getId(),channel.isGroup(),recipientId));

        var dto = new MessageDto(
                command.channelId(),
                savedMessage.getId(),
                savedMessage.getContent(),
                messageMapper.toDto(sender)
                ,
                savedMessage.getAttachments() == null
                        ? List.of()
                        : savedMessage.getAttachments().stream().map(MessageAttachments::getUrl).toList()
        );

        return ApiResponse.success("Message sent", dto);
    }

    private MessageType getMessageType(String messageType) {
        return switch (messageType.toUpperCase()) {
            case "TEXT" -> MessageType.TEXT;
            case "IMAGE" -> MessageType.IMAGE;
            case "FILE" -> MessageType.FILE;
            case "VOICE" -> MessageType.VOICE;
            case "VIDEO" -> MessageType.VIDEO;
            default -> throw new IllegalArgumentException("Unsupported message type: " + messageType);
        };
    }

    private String getRecipientId(Channel channel, String senderId){
        return channel.getMembers().stream()
                .map((c) -> c.getId().getUserId())
                .filter(id -> !id.equals(senderId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Private chat has no recipient"));

    }
}
