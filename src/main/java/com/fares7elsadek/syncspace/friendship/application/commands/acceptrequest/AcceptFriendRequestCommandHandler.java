package com.fares7elsadek.syncspace.friendship.application.commands.acceptrequest;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.domain.events.AcceptFriendRequestEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandHandler;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.api.UserAccessService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AcceptFriendRequestCommandHandler
        implements CommandHandler<AcceptFriendRequestCommand, ApiResponse<String>> {

    private final FriendshipRepository friendshipRepository;
    private final SpringEventPublisher springEventPublisher;
    private final UserAccessService userAccessService;

    @Transactional
    @Override
    public ApiResponse<String> handle(AcceptFriendRequestCommand command) {

        var request = friendshipRepository.findById(command.id()).orElseThrow(
                () -> new FriendshipRequestException(String.format("Request with id %s not found", command.id()))
        );

        var currentUserId = userAccessService.getCurrentUserInfo().getId();

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
        var savedRequest = friendshipRepository.save(request);

        springEventPublisher.publish(
                AcceptFriendRequestEvent.toEntity(savedRequest)
        );

        return ApiResponse
                .success(String.format("Request with id %s has been accepted", request.getId()),null);
    }
}
