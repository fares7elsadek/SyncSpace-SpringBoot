package com.fares7elsadek.syncspace.friendship.queries.listallpendingrequests;

import com.fares7elsadek.syncspace.friendship.model.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record ListAllPendingRequestsQuery() implements Query<ApiResponse<List<FriendShipDto>>> {
}
