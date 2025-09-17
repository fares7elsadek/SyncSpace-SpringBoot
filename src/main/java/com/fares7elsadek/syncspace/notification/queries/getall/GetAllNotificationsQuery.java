package com.fares7elsadek.syncspace.notification.queries.getall;


import com.fares7elsadek.syncspace.notification.model.dtos.NotificationDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetAllNotificationsQuery()
        implements Query<ApiResponse<List<NotificationDto>>> {
}
