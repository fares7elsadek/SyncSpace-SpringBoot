package com.fares7elsadek.syncspace.channel.application.queries.getroom;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetRoomQuery(
        String channelId
) implements Query<ApiResponse<RoomStateDto>> {
}
