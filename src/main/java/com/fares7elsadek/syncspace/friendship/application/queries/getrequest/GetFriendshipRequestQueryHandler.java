package com.fares7elsadek.syncspace.friendship.application.queries.getrequest;

import com.fares7elsadek.syncspace.friendship.api.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.application.mapper.FriendshipMapper;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.QueryHandler;
import com.fares7elsadek.syncspace.shared.exceptions.NotFoundException;
import com.fares7elsadek.syncspace.shared.exceptions.UnauthorizedException;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetFriendshipRequestQueryHandler
        implements QueryHandler<GetFriendshipRequestQuery, ApiResponse<FriendShipDto>> {

    private final FriendshipRepository friendshipRepository;
    private final UserAccessService userAccessService;
    private final FriendshipMapper friendshipMapper;
    @Override
    public ApiResponse<FriendShipDto> handle(GetFriendshipRequestQuery query) {
        var user = userAccessService.getCurrentUserInfo();

        var friendship = friendshipRepository.findById(query.id())
                .orElseThrow(() -> new NotFoundException("Friendship not found"));

        if (
                !friendship.getRequester().getId().equals(user.getId()) &&
                        !friendship.getAddressee().getId().equals(user.getId())
        ) {
            throw new UnauthorizedException("You are not allowed to perform this operation");
        }


        User anotherUser=  friendship.getAddressee().getId().equals(user.getId()) ?
                friendship.getRequester() : friendship.getAddressee();


        var dto = friendshipMapper.toFriendshipDto(friendship,
                anotherUser);

        return ApiResponse.success("Friendship found",dto);
    }
}
