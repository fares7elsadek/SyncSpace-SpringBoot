package com.fares7elsadek.syncspace.messaging.application.commands.attachments.removeattachment;

import com.fares7elsadek.syncspace.messaging.infrastructure.repository.AttachmentRepository;
import com.fares7elsadek.syncspace.messaging.domain.events.RemoveAttachmentEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.AccessDeniedException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoveAttachmentCommandHandler
        implements CommandHandler<RemoveAttachmentCommand, ApiResponse<String>> {

    private final AttachmentRepository attachmentRepository;
    private final UserAccessService userAccessService;
    private final SpringEventPublisher springEventPublisher;

    @Override
    @Transactional
    public ApiResponse<String> handle(RemoveAttachmentCommand command) {
        var user = userAccessService.getCurrentUserInfo();
        var attachment = attachmentRepository.findById(command.attachmentId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Attachment not found (%s)", command.attachmentId())));

        if (!user.getId().equals(attachment.getCreatedBy())) {
            throw new AccessDeniedException("You are not allowed to remove this attachment");
        }

        attachmentRepository.delete(attachment);
        springEventPublisher.publish(RemoveAttachmentEvent.toEvent(user.getId(), command.attachmentId()));

        return ApiResponse.success("Attachment removed successfully", null);
    }

}
