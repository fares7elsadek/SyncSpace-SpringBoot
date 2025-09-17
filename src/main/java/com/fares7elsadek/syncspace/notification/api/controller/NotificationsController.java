package com.fares7elsadek.syncspace.notification.api.controller;

import com.fares7elsadek.syncspace.notification.application.commands.markall.MarkAllAsReadCommand;
import com.fares7elsadek.syncspace.notification.application.commands.markread.MarkReadCommand;
import com.fares7elsadek.syncspace.notification.application.commands.removenotification.RemoveNotificationCommand;
import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.application.queries.getall.GetAllNotificationsQuery;
import com.fares7elsadek.syncspace.notification.application.queries.getunread.GetAllUnReadNotificationsQuery;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.cqrs.QueryBus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationsController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(
            @NotBlank(message = "Notification ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Notification ID must be a valid UUID")
            @PathVariable String notificationId
    ) {
        return ResponseEntity.ok(commandBus.send(new MarkReadCommand(notificationId)));
    }

    @PostMapping("/read")
    public ResponseEntity<ApiResponse<String>> markAllAsRead() {
        return ResponseEntity.ok(commandBus.send(new MarkAllAsReadCommand()));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @NotBlank(message = "Notification ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Notification ID must be a valid UUID")
            @PathVariable String notificationId
    ) {
        return ResponseEntity.ok(commandBus.send(new RemoveNotificationCommand(notificationId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAll(){
        return ResponseEntity.ok(queryBus.send(new GetAllNotificationsQuery()));
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAllUnread(){
        return ResponseEntity.ok(queryBus.send(new GetAllUnReadNotificationsQuery()));
    }

}
