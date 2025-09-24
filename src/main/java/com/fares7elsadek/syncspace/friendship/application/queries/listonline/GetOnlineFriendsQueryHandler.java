package com.fares7elsadek.syncspace.friendship.application.queries.listonline;

import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.application.mapper.FriendshipMapper;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetOnlineFriendsQueryHandler implements QueryHandler<GetOnlineFriendsQuery, ApiResponse<List<FriendShipDto>>> {

    private final FriendshipRepository friendshipRepository;
    private final UserAccessService userAccessService;
    private final FriendshipMapper friendshipMapper;
    @Override
    public ApiResponse<List<FriendShipDto>> handle(GetOnlineFriendsQuery query) {
        return null;
    }
}
