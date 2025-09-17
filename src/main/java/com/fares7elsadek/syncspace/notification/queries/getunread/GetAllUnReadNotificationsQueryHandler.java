package com.fares7elsadek.syncspace.notification.queries.getunread;

import com.fares7elsadek.syncspace.notification.mapper.NotificationMapper;
import com.fares7elsadek.syncspace.notification.model.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.repository.NotificationRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUnReadNotificationsQueryHandler
        implements QueryHandler<GetAllUnReadNotificationsQuery, ApiResponse<List<NotificationDto>>> {

    private final NotificationRepository notificationRepository;
    private final UserAccessService userAccessService;
    private final NotificationMapper notificationMapper;
    @Override
    public ApiResponse<List<NotificationDto>> handle(GetAllUnReadNotificationsQuery query) {
        var user = userAccessService.getCurrentUserInfo();
        var notifications = notificationRepository.findByUserAndRead(user.getId(),false);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        notifications.forEach(notification -> {
            notificationDtos.add(notificationMapper.toNotificationDto(notification));
        });

        return ApiResponse.success("Unread notifications",notificationDtos);
    }
}
