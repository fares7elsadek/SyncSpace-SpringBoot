package com.fares7elsadek.syncspace.friendship.application.queries.listall;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.application.mapper.FriendshipMapper;
import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListAllFriendsQueryHandler
        implements QueryHandler<ListAllFriendsQuery, ApiResponse<List<FriendShipDto>>> {

    private final FriendshipRepository friendshipRepository;
    private final UserAccessService userAccessService;
    private final FriendshipMapper friendshipMapper;
    @Override
    public ApiResponse<List<FriendShipDto>> handle(ListAllFriendsQuery query) {
        var userId = userAccessService.getCurrentUserInfo().getId();
        var friendships = friendshipRepository.findFriendshipsByUserId(userId, FriendShipStatus.ACCEPTED)
                ;

        if (friendships.isEmpty()) {
            return ApiResponse.success("No friendships found", List.of());
        }

        List<FriendShipDto> dto =
                friendships.stream()
                        .map(f -> {
                            var user = f.getAddressee().getId().equals(userId) ?
                                    f.getRequester() : f.getAddressee();
                            return new  FriendShipDto(f.getId(),friendshipMapper.toFriendshipUserDto(user));
                        }).collect(Collectors.toList());

        return ApiResponse.success("All friendships", dto);
    }
}
