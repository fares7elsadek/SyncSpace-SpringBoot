package com.fares7elsadek.syncspace.notification.application.queries.getunread;

import com.fares7elsadek.syncspace.notification.api.dtos.NotificationDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetAllUnReadNotificationsQuery(

) implements Query<ApiResponse<List<NotificationDto>>> {
}
