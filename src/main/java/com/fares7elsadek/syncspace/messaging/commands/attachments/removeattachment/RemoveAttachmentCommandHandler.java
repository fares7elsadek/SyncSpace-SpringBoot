package com.fares7elsadek.syncspace.messaging.commands.attachments.removeattachment;

import com.fares7elsadek.syncspace.messaging.repository.AttachmentRepository;
import com.fares7elsadek.syncspace.messaging.shared.RemoveAttachmentEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.AccessDeniedException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoveAttachmentCommandHandler
        implements CommandHandler<RemoveAttachmentCommand, ApiResponse> {

    private final AttachmentRepository attachmentRepository;
    private final UserValidationService userValidationService;
    private final SpringEventPublisher springEventPublisher;

    @Override
    @Transactional
    public ApiResponse handle(RemoveAttachmentCommand command) {
        var user = userValidationService.getCurrentUserInfo();
        var attachment = attachmentRepository.findById(command.attachmentId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Attachment not found (%s)", command.attachmentId())));

        if (!user.getId().equals(attachment.getCreatedBy())) {
            throw new AccessDeniedException("You are not allowed to remove this attachment");
        }

        attachmentRepository.delete(attachment);
        springEventPublisher.publish(new RemoveAttachmentEvent(command.attachmentId()));

        return ApiResponse.success("Attachment removed successfully", null);
    }

}
