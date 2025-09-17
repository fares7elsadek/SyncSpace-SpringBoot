package com.fares7elsadek.syncspace.messaging.commands.messages.sendmessage;

import com.fares7elsadek.syncspace.channel.api.ChannelAccessService;
import com.fares7elsadek.syncspace.channel.model.Channel;
import com.fares7elsadek.syncspace.messaging.enums.MessageType;
import com.fares7elsadek.syncspace.messaging.mapper.MessageMapper;
import com.fares7elsadek.syncspace.messaging.model.Message;
import com.fares7elsadek.syncspace.messaging.model.MessageAttachments;
import com.fares7elsadek.syncspace.messaging.model.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.repository.AttachmentRepository;
import com.fares7elsadek.syncspace.messaging.repository.MessageRepository;
import com.fares7elsadek.syncspace.messaging.shared.SendMessageEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
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

        springEventPublisher.publish(new SendMessageEvent(command.channelId()
                , savedMessage.getId(),recipientId, channel.isGroup()));

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
