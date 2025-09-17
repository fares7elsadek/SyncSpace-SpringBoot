package com.fares7elsadek.syncspace.friendship.controller;

import com.fares7elsadek.syncspace.friendship.commands.acceptrequest.AcceptFriendRequestCommand;
import com.fares7elsadek.syncspace.friendship.commands.rejectrequest.RejectFriendRequestCommand;
import com.fares7elsadek.syncspace.friendship.commands.removefriendship.RemoveFriendshipCommand;
import com.fares7elsadek.syncspace.friendship.commands.sendrequest.SendFriendRequestCommand;
import com.fares7elsadek.syncspace.friendship.model.dtos.FriendShipDto;
import com.fares7elsadek.syncspace.friendship.queries.listall.ListAllFriendsQuery;
import com.fares7elsadek.syncspace.friendship.queries.listpending.ListAllPendingRequestsQuery;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.cqrs.CommandBus;
import com.fares7elsadek.syncspace.shared.cqrs.QueryBus;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@RequiredArgsConstructor
@Tag(name = "Friendship")
public class FriendshipController {
    private final CommandBus commandBus;
    private final QueryBus queryBus;

    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> sendFriendRequest(
            @NotBlank(message = "User ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(commandBus.send(new SendFriendRequestCommand(userId)));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<String>> removeFriendship(
            @NotBlank(message = "User ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "User ID must be a valid UUID")
            @PathVariable String userId
    ) {
        return ResponseEntity.ok(commandBus.send(new RemoveFriendshipCommand(userId)));
    }

    @PostMapping("/reject/{requestId}")
    public ResponseEntity<ApiResponse<String>> rejectFriendship(
            @NotBlank(message = "Request ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Request ID must be a valid UUID")
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(commandBus.send(new RejectFriendRequestCommand(requestId)));
    }

    @PostMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse<String>> acceptFriendship(
            @NotBlank(message = "Request ID cannot be blank")
            @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Request ID must be a valid UUID")
            @PathVariable String requestId
    ) {
        return ResponseEntity.ok(commandBus.send(new AcceptFriendRequestCommand(requestId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FriendShipDto>>> listAllFriends(){
        return ResponseEntity.ok(queryBus.send(new ListAllFriendsQuery()));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<FriendShipDto>>> listAllPendingFriends(){
        return ResponseEntity.ok(queryBus.send(new ListAllPendingRequestsQuery()));
    }
}
