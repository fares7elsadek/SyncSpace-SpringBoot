package com.fares7elsadek.syncspace.friendship.application.queries.listall;

import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

import java.util.List;

public record ListAllFriendsQuery() implements Query<ApiResponse<List<FriendShipDto>>> {
}
