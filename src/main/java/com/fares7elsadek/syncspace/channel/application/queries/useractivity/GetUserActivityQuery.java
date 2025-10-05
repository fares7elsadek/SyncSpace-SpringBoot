package com.fares7elsadek.syncspace.channel.application.queries.useractivity;

import com.fares7elsadek.syncspace.channel.api.dtos.RoomStateDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record GetUserActivityQuery() implements Query<ApiResponse<List<RoomStateDto>>> {
}
