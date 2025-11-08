package com.fares7elsadek.syncspace.friendship.application.commands.sendrequest;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
import com.fares7elsadek.syncspace.friendship.domain.events.SendFriendRequestEvent;
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
class SendFriendRequestHandlerTest {

    @Mock
    private UserAccessService userAccessService;

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private SpringEventPublisher springEventPublisher;

    @InjectMocks
    private SendFriendRequestHandler handler;

    private User currentUser;
    private User targetUser;
    private String currentUserId;
    private String targetUserId;
    private String targetUsername;
    private Friendships savedFriendship;

    @BeforeEach
    void setUp() {
        currentUserId = "current-user-123";
        targetUserId = "target-user-456";
        targetUsername = "targetUsername";

        currentUser = User.builder()
                .id(currentUserId)
                .username("currentUsername")
                .build();

        targetUser = User.builder()
                .id(targetUserId)
                .username(targetUsername)
                .build();

        savedFriendship = Friendships.builder()
                .id("friendship-789")
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should successfully send friend request to new user")
    void shouldSendFriendRequestSuccessfully() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        ApiResponse<String> response = handler.handle(command);

        // Assert
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals("Friendship request sent successfully", response.message());
        assertNull(response.data());

        // Verify interactions
        verify(userAccessService, times(1)).getByUsername(targetUsername);
        verify(userAccessService, times(1)).getCurrentUserInfo();
        verify(friendshipRepository, times(1)).findFriendshipRequest(currentUserId, targetUserId);
        verify(friendshipRepository, times(1)).save(any(Friendships.class));
        verify(springEventPublisher, times(1)).publish(any(SendFriendRequestEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when trying to send friend request to yourself")
    void shouldThrowExceptionWhenSendingRequestToSelf() {
        // Arrange
        String sameUserId = "same-user-123";
        User sameUser = User.builder()
                .id(sameUserId)
                .username("sameUsername")
                .build();

        SendFriendRequestCommand command = new SendFriendRequestCommand("sameUsername");

        when(userAccessService.getByUsername("sameUsername")).thenReturn(sameUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(sameUser);

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals("Cannot send friend request to yourself", exception.getMessage());

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).findFriendshipRequest(anyString(), anyString());
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when pending friend request already exists")
    void shouldThrowExceptionWhenPendingRequestExists() {
        // Arrange
        Friendships existingPendingRequest = Friendships.builder()
                .id("existing-123")
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.PENDING)
                .build();

        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.of(existingPendingRequest));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals("Friendship request already exists", exception.getMessage());

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when accepted friendship already exists")
    void shouldThrowExceptionWhenAcceptedFriendshipExists() {
        // Arrange
        Friendships existingAcceptedFriendship = Friendships.builder()
                .id("existing-123")
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.ACCEPTED)
                .build();

        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.of(existingAcceptedFriendship));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals("Friendship request already accepted", exception.getMessage());

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should throw exception when rejected friendship already exists")
    void shouldThrowExceptionWhenRejectedFriendshipExists() {
        // Arrange
        Friendships existingRejectedFriendship = Friendships.builder()
                .id("existing-123")
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.REJECTED)
                .build();

        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.of(existingRejectedFriendship));

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals("Friendship request already rejected", exception.getMessage());

        // Verify no save or publish occurred
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should build friendship with correct properties")
    void shouldBuildFriendshipWithCorrectProperties() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());

        ArgumentCaptor<Friendships> friendshipCaptor = ArgumentCaptor.forClass(Friendships.class);
        when(friendshipRepository.save(friendshipCaptor.capture())).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert
        Friendships capturedFriendship = friendshipCaptor.getValue();
        assertNotNull(capturedFriendship);
        assertEquals(currentUser, capturedFriendship.getRequester());
        assertEquals(targetUser, capturedFriendship.getAddressee());
        assertEquals(FriendShipStatus.PENDING, capturedFriendship.getFriendShipStatus());
    }

    @Test
    @DisplayName("Should publish event with saved friendship")
    void shouldPublishEventWithSavedFriendship() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert
        ArgumentCaptor<SendFriendRequestEvent> eventCaptor =
                ArgumentCaptor.forClass(SendFriendRequestEvent.class);
        verify(springEventPublisher).publish(eventCaptor.capture());

        SendFriendRequestEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        // Verify event properties based on SendFriendRequestEvent.toEvent() implementation
    }

    @Test
    @DisplayName("Should throw exception when target user not found")
    void shouldThrowExceptionWhenTargetUserNotFound() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand("nonexistentUser");

        when(userAccessService.getByUsername("nonexistentUser"))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify no further operations occurred
        verify(userAccessService, never()).getCurrentUserInfo();
        verify(friendshipRepository, never()).findFriendshipRequest(anyString(), anyString());
        verify(friendshipRepository, never()).save(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should handle repository save failure")
    void shouldHandleRepositorySaveFailure() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify event was not published
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should handle event publishing failure")
    void shouldHandleEventPublishingFailure() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);
        doThrow(new RuntimeException("Event publishing failed"))
                .when(springEventPublisher).publish(any(SendFriendRequestEvent.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify save was called but transaction should rollback
        verify(friendshipRepository, times(1)).save(any(Friendships.class));
    }

    @Test
    @DisplayName("Should lookup user by username from command")
    void shouldLookupUserByUsernameFromCommand() {
        // Arrange
        String specificUsername = "specificUser123";
        User specificTargetUser = User.builder()
                .id("specific-target-id")
                .username(specificUsername)
                .build();

        SendFriendRequestCommand command = new SendFriendRequestCommand(specificUsername);

        when(userAccessService.getByUsername(specificUsername)).thenReturn(specificTargetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert
        verify(userAccessService, times(1)).getByUsername(specificUsername);
    }

    @Test
    @DisplayName("Should verify exact order of operations")
    void shouldVerifyOrderOfOperations() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert - verify order of operations
        var inOrder = inOrder(userAccessService, friendshipRepository, springEventPublisher);
        inOrder.verify(userAccessService).getByUsername(targetUsername);
        inOrder.verify(userAccessService).getCurrentUserInfo();
        inOrder.verify(friendshipRepository).findFriendshipRequest(currentUserId, targetUserId);
        inOrder.verify(friendshipRepository).save(any(Friendships.class));
        inOrder.verify(springEventPublisher).publish(any(SendFriendRequestEvent.class));
    }

    @Test
    @DisplayName("Should check for existing request before creating new one")
    void shouldCheckForExistingRequestBeforeCreating() {
        // Arrange
        SendFriendRequestCommand command = new SendFriendRequestCommand(targetUsername);

        when(userAccessService.getByUsername(targetUsername)).thenReturn(targetUser);
        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUser);
        when(friendshipRepository.findFriendshipRequest(currentUserId, targetUserId))
                .thenReturn(Optional.empty());
        when(friendshipRepository.save(any(Friendships.class))).thenReturn(savedFriendship);

        // Act
        handler.handle(command);

        // Assert - verify check happened before save
        var inOrder = inOrder(friendshipRepository);
        inOrder.verify(friendshipRepository).findFriendshipRequest(currentUserId, targetUserId);
        inOrder.verify(friendshipRepository).save(any(Friendships.class));
    }
}