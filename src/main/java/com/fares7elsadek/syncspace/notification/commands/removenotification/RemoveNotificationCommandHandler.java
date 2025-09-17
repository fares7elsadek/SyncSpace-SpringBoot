package com.fares7elsadek.syncspace.notification.commands.removenotification;

import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.exceptions.AccessDeniedException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoveNotificationCommandHandler
        implements CommandHandler<RemoveNotificationCommand,
        ApiResponse<String>> {

    private final NotificationRepository notificationRepository;
    private final UserAccessService userAccessService;
    @Override
    public ApiResponse<String> handle(RemoveNotificationCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        var user = userAccessService.getCurrentUserInfo();

        if(!notification.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("You are not allowed to delete this notification");

        notificationRepository.delete(notification);

        return ApiResponse.success("Notification deleted successfully",null);
    }
}
