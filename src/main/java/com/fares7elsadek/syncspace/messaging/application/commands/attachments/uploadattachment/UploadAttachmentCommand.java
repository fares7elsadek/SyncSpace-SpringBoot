package com.fares7elsadek.syncspace.messaging.application.commands.attachments.uploadattachment;

import com.fares7elsadek.syncspace.shared.cqrs.Command;
import com.fares7elsadek.syncspace.shared.validators.FileSize;
import com.fares7elsadek.syncspace.shared.validators.FileType;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UploadAttachmentCommand(
        @NotNull(message = "Attachment is required")
        @FileSize(max = 50 * 1024 * 1024, message = "File size must not exceed 50MB")
        @FileType(allowed = {"image/png", "image/jpeg", "application/pdf"})
        MultipartFile attachment
) implements Command {
}
