package com.fares7elsadek.syncspace.notification.application.queries.getall;


import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetAllNotificationsQuery(
        int page,
        int size
)
        implements Query<ApiResponse<List<NotificationDto>>> {
}
