package com.fares7elsadek.syncspace.messaging.queries.getmessages;

import com.fares7elsadek.syncspace.messaging.model.dtos.PaginatedMessageResponse;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetMessagesQuery(
        String channelId,
        String cursor,
        int size
) implements Query<ApiResponse<PaginatedMessageResponse>> {
}
