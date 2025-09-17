package com.fares7elsadek.syncspace.messaging.api.controller;

import com.fares7elsadek.syncspace.messaging.application.commands.attachments.removeattachment.RemoveAttachmentCommand;
import com.fares7elsadek.syncspace.messaging.application.commands.attachments.uploadattachment.UploadAttachmentCommand;
import com.fares7elsadek.syncspace.messaging.application.commands.messages.deletemessage.DeleteMessageCommand;
import com.fares7elsadek.syncspace.messaging.application.commands.messages.sendmessage.SendMessageCommand;
import com.fares7elsadek.syncspace.messaging.api.dtos.AttachmentDto;
import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.messaging.api.dtos.PaginatedMessageResponse;
import com.fares7elsadek.syncspace.messaging.application.queries.getmessages.GetMessagesQuery;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.cqrs.QueryBus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Tag(name = "Messages")
public class MessageController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping("/attachment")
    public ResponseEntity<ApiResponse<AttachmentDto>> uploadAttachment(
            @RequestBody @Valid UploadAttachmentCommand command) {
        return ResponseEntity.ok(commandBus.send(command));
    }

    @DeleteMapping("/attachment/{attachmentId}")
    public ResponseEntity<ApiResponse<AttachmentDto>> deleteAttachment(
            @PathVariable
            @NotBlank(message = "Attachment ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Attachment ID must be a valid UUID")
            String attachmentId) {
        return ResponseEntity.ok(commandBus.send(new RemoveAttachmentCommand(attachmentId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDto>> sendMessage(@RequestBody @Valid SendMessageCommand command) {
        return ResponseEntity.ok(commandBus.send(command));
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable @NotBlank(message = "Message ID is required") String messageId
    ) {
        return ResponseEntity.ok(commandBus.send(new DeleteMessageCommand(messageId)));
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<ApiResponse<PaginatedMessageResponse>> getMessages(
            @PathVariable
            @NotBlank(message = "Channel ID is required")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Channel ID must be a valid UUID")
            String channelId,

            @RequestParam(defaultValue = "")
            String cursor,

            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "Size must be at least 1")
            @Max(value = 100, message = "Size must not exceed 100")
            int size
    ) {
        return ResponseEntity.ok(queryBus.send(new GetMessagesQuery(channelId, cursor, size)));
    }

}
