package com.fares7elsadek.syncspace.messaging.commands.messages.deletemessage;

import com.fares7elsadek.syncspace.messaging.repository.MessageRepository;
import com.fares7elsadek.syncspace.messaging.shared.DeleteMessageEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.AccessDeniedException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeleteMessageCommandHandler
        implements CommandHandler<DeleteMessageCommand, ApiResponse<String>> {

    private final UserValidationService userValidationService;
    private final MessageRepository messageRepository;
    private final SpringEventPublisher springEventPublisher;

    @Override
    public ApiResponse<String> handle(DeleteMessageCommand command) {
        var user = userValidationService.getCurrentUserInfo();
        var message = messageRepository.findById(command.messageId())
                .orElseThrow(() -> new NotFoundException(String.format("Message not found %s", command.messageId())));

        if(!message.getCreatedBy().equals(user.getId()))
            throw new AccessDeniedException(String.format("You don't have permission to perform this action"));

        messageRepository.delete(message);
        springEventPublisher.publish(new DeleteMessageEvent(message.getId()));

        return ApiResponse.success("Message deleted",null);
    }
}
