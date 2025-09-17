package com.fares7elsadek.syncspace.notification.commands.markread;

import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.exceptions.AccessDeniedException;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MarkReadCommandHandler
        implements CommandHandler<MarkReadCommand,ApiResponse<String>> {

    private final NotificationRepository notificationRepository;
    private final UserAccessService userAccessService;
    @Override
    @Transactional
    public ApiResponse<String> handle(MarkReadCommand command) {
        var notification = notificationRepository.findById(command.notificationId())
                .orElseThrow(() -> new NotFoundException("Notification not found"));

        var user = userAccessService.getCurrentUserInfo();

        if(!notification.getUser().getId().equals(user.getId()))
            throw new AccessDeniedException("You are not allowed to mark this read");

        notification.setRead(true);

        return ApiResponse.success("Marked successfully as read",null);
    }
}
