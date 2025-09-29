package com.fares7elsadek.syncspace.messaging.application.queries.getmessage;

import com.fares7elsadek.syncspace.messaging.api.dtos.MessageDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetMessageQuery(
        String id
) implements Query<ApiResponse<MessageDto>> {
}
