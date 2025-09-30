package com.fares7elsadek.syncspace.notification.application.queries.getall;

import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.notification.application.mapper.NotificationMapper;
import com.fares7elsadek.syncspace.notification.infrastructure.repository.NotificationRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllNotificationsQueryHandler implements QueryHandler<GetAllNotificationsQuery,
        ApiResponse<List<NotificationDto>>> {

    private final NotificationRepository notificationRepository;
    private final UserAccessService userAccessService;
    private final NotificationMapper notificationMapper;
    @Override
    public ApiResponse<List<NotificationDto>> handle(GetAllNotificationsQuery query) {
        var user = userAccessService.getCurrentUserInfo();
        PageRequest pageRequest = PageRequest.of(query.page(), query.size());
        var notifications = notificationRepository.findByUser(user.getId(),pageRequest);
        List<NotificationDto> notificationDtos = new ArrayList<>();
        notifications.forEach(notification -> {
            notificationDtos.add(notificationMapper.toNotificationDto(notification));
        });
        var count = notificationRepository.findCountByUser(user.getId());
        return ApiResponse.success(String.format("%s",count),notificationDtos);
    }
}
