package com.fares7elsadek.syncspace.friendship.commands.acceptrequest;

import com.fares7elsadek.syncspace.friendship.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.shared.AcceptFriendRequestEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.api.UserValidationService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcceptFriendRequestCommandHandler
        implements CommandHandler<AcceptFriendRequestCommand, ApiResponse<String>> {

    private final FriendshipRepository friendshipRepository;
    private final SpringEventPublisher springEventPublisher;
    private final UserValidationService userValidationService;

    @Transactional
    @Override
    public ApiResponse<String> handle(AcceptFriendRequestCommand command) {

        var request = friendshipRepository.findById(command.id()).orElseThrow(
                () -> new FriendshipRequestException(String.format("Request with id %s not found", command.id()))
        );

        var currentUserId = userValidationService.getCurrentUserInfo().getId();

        if (!currentUserId.equals(request.getAddressee().getId())) {
            throw new FriendshipRequestException(
                    String.format("User %s is not allowed to accept request %s", currentUserId, request.getId())
            );
        }

        if (request.getFriendShipStatus() == FriendShipStatus.ACCEPTED) {
            throw new FriendshipRequestException(
                    String.format("Request with id %s is already accepted", request.getId()));
        }
        if (request.getFriendShipStatus() == FriendShipStatus.REJECTED) {
            throw new FriendshipRequestException(
                    String.format("Request with id %s is already rejected", request.getId()));
        }

        request.setFriendShipStatus(FriendShipStatus.ACCEPTED);
        friendshipRepository.save(request);

        springEventPublisher.publish(
                new AcceptFriendRequestEvent(currentUserId,request.getAddressee().getId())
        );

        return ApiResponse
                .success(String.format("Request with id %s has been accepted", request.getId()),null);
    }
}
