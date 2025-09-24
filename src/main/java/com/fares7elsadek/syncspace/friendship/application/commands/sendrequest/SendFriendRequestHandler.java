package com.fares7elsadek.syncspace.friendship.application.commands.sendrequest;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.domain.events.SendFriendRequestEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import com.fares7elsadek.syncspace.user.domain.model.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("sendFriendRequestCommandHandler")
public class SendFriendRequestHandler implements
        CommandHandler<SendFriendRequestCommand, ApiResponse<String>> {

    private final UserAccessService userAccessService;
    private final FriendshipRepository friendshipRepository;
    private final SpringEventPublisher springEventPublisher;

    @Transactional
    @Override
    public ApiResponse<String> handle(SendFriendRequestCommand command) {

        var targetUser = userAccessService.getByUsername(command.username());
        var currentUser = userAccessService.getCurrentUserInfo();

        if(targetUser.getId().equals(currentUser.getId())){
            throw new FriendshipRequestException("Cannot send friend request to yourself");
        }

        handleExistingRequest(currentUser.getId(),targetUser.getId());
        var savedRequest = friendshipRepository.save(buildFriendships(currentUser,targetUser));

        springEventPublisher
                .publish(SendFriendRequestEvent.toEvent(savedRequest));

        return ApiResponse
                .success("Friendship request sent successfully",null);
    }

    private Friendships buildFriendships(User currentUser, User targetUser) {
        return Friendships.builder()
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.PENDING)
                .build();
    }

    private void handleExistingRequest(String currentUserId, String targetUserId) {
        friendshipRepository.findFriendshipRequest(currentUserId, targetUserId)
                .ifPresent(friendship -> {
                    throw switch (friendship.getFriendShipStatus()) {
                        case PENDING -> new FriendshipRequestException("Friendship request already exists");
                        case ACCEPTED -> new FriendshipRequestException("Friendship request already accepted");
                        case REJECTED -> new FriendshipRequestException("Friendship request already rejected");
                    };
                });
    }

}
