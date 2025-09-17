package com.fares7elsadek.syncspace.messaging.application.commands.attachments.uploadattachment;

import com.fares7elsadek.syncspace.messaging.domain.model.MessageAttachments;
import com.fares7elsadek.syncspace.messaging.api.dtos.AttachmentDto;
import com.fares7elsadek.syncspace.messaging.infrastructure.repository.AttachmentRepository;
import com.fares7elsadek.syncspace.messaging.domain.events.UploadAttachmentEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.api.StorageService;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.SaveFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UploadAttachmentCommandHandler implements CommandHandler<UploadAttachmentCommand, ApiResponse<AttachmentDto>> {
    private final StorageService storageService;
    private final AttachmentRepository attachmentRepository;
    private final SpringEventPublisher springEventPublisher;
    @Override
    @Transactional
    public ApiResponse<AttachmentDto> handle(UploadAttachmentCommand command) {
        try{
            String storageUrl = storageService.upload(command.attachment());

            String extension = Optional.ofNullable(
                            command.attachment().getOriginalFilename()
                    ).filter(f -> f.contains("."))
                    .map(f -> f.substring(f.lastIndexOf(".")))
                    .orElse("");
            String fileName = UUID.randomUUID() + extension;

            MessageAttachments attachment = MessageAttachments.builder()
                    .fileName(fileName)
                    .originalFileName(command.attachment().getOriginalFilename())
                    .fileSize(command.attachment().getSize())
                    .mimeType(command.attachment().getContentType())
                    .url(storageUrl)
                    .build();

            var file = attachmentRepository.save(attachment);

            springEventPublisher.publish(
                    UploadAttachmentEvent.toEvent(file));

            return ApiResponse.success("Uploaded",
            new AttachmentDto(file.getId(),file.getFileName(),file.getOriginalFileName(),file.getUrl()));

        }catch(Exception ex){
            throw new SaveFileException("Saving file failed");
        }
    }
}
