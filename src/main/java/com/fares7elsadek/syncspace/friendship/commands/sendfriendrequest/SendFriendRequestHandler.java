package com.fares7elsadek.syncspace.friendship.commands.sendfriendrequest;

import com.fares7elsadek.syncspace.friendship.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.model.Friendships;
import com.fares7elsadek.syncspace.friendship.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.shared.SendFriendRequestEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import com.fares7elsadek.syncspace.user.model.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SendFriendRequestHandler implements
        CommandHandler<SendFriendRequestCommand, ApiResponse<String>> {

    private final UserValidationService userValidationService;
    private final FriendshipRepository friendshipRepository;
    private final SpringEventPublisher springEventPublisher;

    @Transactional
    @Override
    public ApiResponse<String> handle(SendFriendRequestCommand command) {

        var targetUser = userValidationService.getUserInfo(command.userId());
        var currentUser = userValidationService.getCurrentUserInfo();

        if(targetUser.getId().equals(currentUser.getId())){
            throw new FriendshipRequestException("Cannot send friend request to yourself");
        }

        handleExistingRequest(currentUser.getId(),targetUser.getId());
        friendshipRepository.save(buildFriendships(currentUser,targetUser));

        springEventPublisher
                .publish(new SendFriendRequestEvent(currentUser.getId(),targetUser.getId()));

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
