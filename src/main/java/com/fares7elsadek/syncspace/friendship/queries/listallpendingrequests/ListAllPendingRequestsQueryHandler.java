package com.fares7elsadek.syncspace.friendship.queries.listallpendingrequests;

import com.fares7elsadek.syncspace.friendship.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.mapper.FriendshipMapper;
import com.fares7elsadek.syncspace.friendship.model.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ListAllPendingRequestsQueryHandler
        implements QueryHandler<ListAllPendingRequestsQuery, ApiResponse<List<FriendShipDto>>> {

    private final FriendshipRepository friendshipRepository;
    private final UserValidationService userValidationService;
    private final FriendshipMapper friendshipMapper;
    @Override
    public ApiResponse<List<FriendShipDto>> handle(ListAllPendingRequestsQuery query) {
        var userId = userValidationService.getCurrentUserInfo().getId();
        var friendships = friendshipRepository.findFriendshipsByUserId(userId, FriendShipStatus.PENDING)
                ;

        if (friendships.isEmpty()) {
            return ApiResponse.success("No pending friendships found", List.of());
        }

        List<FriendShipDto> dto =
                friendships.stream()
                        .map(f -> {
                            var user = f.getAddressee().getId().equals(userId) ?
                                    f.getRequester() : f.getAddressee();
                            return new  FriendShipDto(f.getId(),friendshipMapper.toFriendshipUserDto(user));
                        }).collect(Collectors.toList());

        return ApiResponse.success("All pending friendships", dto);
    }
}
