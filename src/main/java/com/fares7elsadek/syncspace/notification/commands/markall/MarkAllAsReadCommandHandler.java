package com.fares7elsadek.syncspace.notification.commands.markall;

import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MarkAllAsReadCommandHandler implements CommandHandler<MarkAllAsReadCommand,
        ApiResponse<String>> {
    private final NotificationRepository notificationRepository;
    private final UserAccessService userAccessService;

    @Override
    public ApiResponse<String> handle(MarkAllAsReadCommand command) {
        var user = userAccessService.getCurrentUserInfo();
        var notifications = notificationRepository.findByUserAndRead(user.getId(), false);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
        return ApiResponse.success("Marked all as read successfully",null);
    }
}
