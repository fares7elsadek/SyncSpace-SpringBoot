package com.fares7elsadek.syncspace.friendship.application.queries.getrequest;

import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.Query;

public record GetFriendshipRequestQuery(
        String id
) implements Query<ApiResponse<FriendShipDto>> {
}
