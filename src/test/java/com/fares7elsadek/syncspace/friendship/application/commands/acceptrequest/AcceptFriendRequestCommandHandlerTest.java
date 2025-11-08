package com.fares7elsadek.syncspace.friendship.application.commands.acceptrequest;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.domain.events.AcceptFriendRequestEvent;
import com.fares7elsadek.syncspace.shared.api.ApiResponse;
import com.fares7elsadek.syncspace.shared.events.SpringEventPublisher;
import com.fares7elsadek.syncspace.shared.exceptions.FriendshipRequestException;
import com.fares7elsadek.syncspace.user.domain.model.User;
import com.fares7elsadek.syncspace.user.shared.UserAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AcceptFriendRequestCommandHandlerTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private SpringEventPublisher springEventPublisher;

    @Mock
    private UserAccessService userAccessService;

    @InjectMocks
    private AcceptFriendRequestCommandHandler handler;

    private User requester;
    private User addressee;
    private Friendships pendingFriendship;
    private String requestId;
    private String currentUserId;

    @BeforeEach
    void setUp() {
        requestId = "request-123";
        currentUserId = "user-456";
        String requesterId = "user-789";

        requester = User.builder()
                .id(requesterId)
                .build();

        addressee = User.builder()
                .id(currentUserId)
                .build();

        pendingFriendship = Friendships.builder()
                .id(requestId)
                .requester(requester)
                .addressee(addressee)
                .friendShipStatus(FriendShipStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully accept a pending friend request")
    void shouldAcceptPendingFriendRequest() {
        // Arrange
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(currentUserId).build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(pendingFriendship);

        // Act
        ApiResponse<String> response = handler.handle(command);

        // Assert
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals(String.format("Request with id %s has been accepted", requestId), response.message());
        assertNull(response.data());

        // Verify friendship status was updated
        assertEquals(FriendShipStatus.ACCEPTED, pendingFriendship.getFriendShipStatus());

        // Verify repository interactions
        verify(friendshipRepository, times(1)).findById(requestId);
        verify(friendshipRepository, times(1)).save(pendingFriendship);

        // Verify event was published
        ArgumentCaptor<AcceptFriendRequestEvent> eventCaptor = ArgumentCaptor.forClass(AcceptFriendRequestEvent.class);
        verify(springEventPublisher, times(1)).publish(eventCaptor.capture());
    }

    @Test
    @DisplayName("Should throw exception when friend request is not found")
    void shouldThrowExceptionWhenRequestNotFound() {
        // Arrange
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.empty());

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals(String.format("Request with id %s not found", requestId), exception.getMessage());

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when current user is not the addressee")
    void shouldThrowExceptionWhenUserNotAddressee() {
        // Arrange
        String unauthorizedUserId = "unauthorized-user";
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(unauthorizedUserId).build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                String.format("User %s is not allowed to accept request %s", unauthorizedUserId, requestId),
                exception.getMessage()
        );

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when request is already accepted")
    void shouldThrowExceptionWhenRequestAlreadyAccepted() {
        // Arrange
        pendingFriendship.setFriendShipStatus(FriendShipStatus.ACCEPTED);
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(currentUserId).build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                String.format("Request with id %s is already accepted", requestId),
                exception.getMessage()
        );

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when request is already rejected")
    void shouldThrowExceptionWhenRequestAlreadyRejected() {
        // Arrange
        pendingFriendship.setFriendShipStatus(FriendShipStatus.REJECTED);
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(currentUserId).build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                String.format("Request with id %s is already rejected", requestId),
                exception.getMessage()
        );

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should verify correct event is published with saved friendship")
    void shouldPublishCorrectEvent() {
        // Arrange
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(currentUserId).build();

        Friendships savedFriendship = Friendships.builder()
                .id(requestId)
                .requester(requester)
                .addressee(addressee)
                .friendShipStatus(FriendShipStatus.ACCEPTED)
                .build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert
        ArgumentCaptor<AcceptFriendRequestEvent> eventCaptor =
                ArgumentCaptor.forClass(AcceptFriendRequestEvent.class);
        verify(springEventPublisher).publish(eventCaptor.capture());

        AcceptFriendRequestEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        // Add more assertions based on your AcceptFriendRequestEvent.toEntity() implementation
    }

    @Test
    @DisplayName("Should handle repository save failure gracefully")
    void shouldHandleRepositorySaveFailure() {
        // Arrange
        AcceptFriendRequestCommand command = new AcceptFriendRequestCommand(requestId);
        User userInfo = User.builder().id(currentUserId).build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(userInfo);
        when(friendshipRepository.findById(requestId)).thenReturn(Optional.of(pendingFriendship));
        when(friendshipRepository.save(any(Friendships.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify event was not published
        verify(springEventPublisher, never()).publish(any());
    }
}