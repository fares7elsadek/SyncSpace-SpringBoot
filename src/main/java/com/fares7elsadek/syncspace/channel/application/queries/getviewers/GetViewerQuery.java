package com.fares7elsadek.syncspace.channel.application.queries.getviewers;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomViewerDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetViewerQuery(
        String channelId
) implements Query<ApiResponse<List<RoomViewerDto>>> {
}
