package com.fares7elsadek.syncspace.friendship.commands.removefriendship;

import com.fares7elsadek.syncspace.friendship.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.shared.RemoveFriendshipEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RemoveFriendshipCommandHandler
        implements CommandHandler<RemoveFriendshipCommand, ApiResponse<String>> {

    private final FriendshipRepository friendshipRepository;
    private final SpringEventPublisher springEventPublisher;
    private final UserAccessService userAccessService;

    @Transactional
    @Override
    public ApiResponse<String> handle(RemoveFriendshipCommand command) {

        var targetUser = userAccessService.getUserInfo(command.userId());
        var currentUser = userAccessService.getCurrentUserInfo();

        var request = friendshipRepository.findFriendshipBetweenUsers(currentUser.getId(),
                targetUser.getId()).orElseThrow(
                () -> new FriendshipRequestException(String.format("Friendship between users %s and %s not found"
                        , currentUser.getId(), targetUser.getId()))
        );

        friendshipRepository.delete(request);

        springEventPublisher.publish(
                new RemoveFriendshipEvent(currentUser.getId(),targetUser.getId())
        );

        return ApiResponse
                .success(String.format("Request with id %s has been deleted successfully"
                        , request.getId()),null);
    }
}
