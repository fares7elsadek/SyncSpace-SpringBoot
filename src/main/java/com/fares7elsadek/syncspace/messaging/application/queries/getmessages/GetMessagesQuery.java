package com.fares7elsadek.syncspace.messaging.application.queries.getmessages;

import com.fares7elsadek.syncspace.messaging.api.dtos.PaginatedMessageResponse;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetMessagesQuery(
        String channelId,
        String cursor,
        int size
) implements Query<ApiResponse<PaginatedMessageResponse>> {
}
