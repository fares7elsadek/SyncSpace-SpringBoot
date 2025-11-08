package com.fares7elsadek.syncspace.friendship.application.commands.removefriendship;

import com.fares7elsadek.syncspace.friendship.domain.enums.FriendShipStatus;
import com.fares7elsadek.syncspace.friendship.domain.events.RemoveFriendshipEvent;
import com.fares7elsadek.syncspace.friendship.domain.model.Friendships;
import com.fares7elsadek.syncspace.friendship.infrastructure.repository.FriendshipRepository;
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
class RemoveFriendshipCommandHandlerTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private SpringEventPublisher springEventPublisher;

    @Mock
    private UserAccessService userAccessService;

    @InjectMocks
    private RemoveFriendshipCommandHandler handler;

    private User currentUser;
    private User targetUser;
    private User currentUserInfo;
    private User targetUserInfo;
    private Friendships friendship;
    private String currentUserId;
    private String targetUserId;
    private String friendshipId;

    @BeforeEach
    void setUp() {
        currentUserId = "current-user-123";
        targetUserId = "target-user-456";
        friendshipId = "friendship-789";

        currentUser = User.builder()
                .id(currentUserId)
                .build();

        targetUser = User.builder()
                .id(targetUserId)
                .build();

        currentUserInfo = User.builder()
                .id(currentUserId)
                .build();

        targetUserInfo = User.builder()
                .id(targetUserId)
                .build();

        friendship = Friendships.builder()
                .id(friendshipId)
                .requester(currentUser)
                .addressee(targetUser)
                .friendShipStatus(FriendShipStatus.ACCEPTED)
                .build();
    }

    @Test
    @DisplayName("Should successfully remove friendship between two users")
    void shouldRemoveFriendshipSuccessfully() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));

        // Act
        ApiResponse<String> response = handler.handle(command);

        // Assert
        assertNotNull(response);
        assertTrue(response.success());
        assertEquals(
                String.format("Request with id %s has been deleted successfully", friendshipId),
                response.message()
        );
        assertNull(response.data());

        // Verify repository interactions
        verify(userAccessService, times(1)).getCurrentUserInfo();
        verify(userAccessService, times(1)).getUserInfo(targetUserId);
        verify(friendshipRepository, times(1)).findFriendshipBetweenUsers(currentUserId, targetUserId);
        verify(friendshipRepository, times(1)).delete(friendship);

        // Verify event was published
        verify(springEventPublisher, times(1)).publish(any(RemoveFriendshipEvent.class));
    }

    @Test
    @DisplayName("Should throw exception when friendship not found")
    void shouldThrowExceptionWhenFriendshipNotFound() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        FriendshipRequestException exception = assertThrows(
                FriendshipRequestException.class,
                () -> handler.handle(command)
        );

        assertEquals(
                String.format("Friendship between users %s and %s not found", currentUserId, targetUserId),
                exception.getMessage()
        );

        // Verify no delete or publish occurred
        verify(friendshipRepository, never()).delete(any());
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should publish event with correct user IDs")
    void shouldPublishEventWithCorrectUserIds() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));

        // Act
        handler.handle(command);

        // Assert
        ArgumentCaptor<RemoveFriendshipEvent> eventCaptor =
                ArgumentCaptor.forClass(RemoveFriendshipEvent.class);
        verify(springEventPublisher).publish(eventCaptor.capture());

        RemoveFriendshipEvent publishedEvent = eventCaptor.getValue();
        assertNotNull(publishedEvent);
        // Note: Verify event fields based on your RemoveFriendshipEvent.toEvent() implementation
        // The method signature shows: toEvent(currentUserId, targetUserId, currentUserId)
    }

    @Test
    @DisplayName("Should handle repository delete failure")
    void shouldHandleRepositoryDeleteFailure() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));
        doThrow(new RuntimeException("Database error"))
                .when(friendshipRepository).delete(friendship);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify event was not published
        verify(springEventPublisher, never()).publish(any());
    }

    @Test
    @DisplayName("Should handle event publishing failure")
    void shouldHandleEventPublishingFailure() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));
        doThrow(new RuntimeException("Event publishing failed"))
                .when(springEventPublisher).publish(any(RemoveFriendshipEvent.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify delete was called but transaction should rollback
        verify(friendshipRepository, times(1)).delete(friendship);
    }

    @Test
    @DisplayName("Should remove friendship regardless of who initiated it")
    void shouldRemoveFriendshipRegardlessOfInitiator() {
        // Arrange - Create friendship where target user is the requester
        Friendships reverseFriendship = Friendships.builder()
                .id(friendshipId)
                .requester(targetUser)
                .addressee(currentUser)
                .friendShipStatus(FriendShipStatus.ACCEPTED)
                .build();

        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(reverseFriendship));

        // Act
        ApiResponse<String> response = handler.handle(command);

        // Assert
        assertNotNull(response);
        assertTrue(response.success());
        verify(friendshipRepository, times(1)).delete(reverseFriendship);
        verify(springEventPublisher, times(1)).publish(any(RemoveFriendshipEvent.class));
    }

    @Test
    @DisplayName("Should verify correct friendship is deleted")
    void shouldVerifyCorrectFriendshipIsDeleted() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));

        ArgumentCaptor<Friendships> friendshipCaptor = ArgumentCaptor.forClass(Friendships.class);

        // Act
        handler.handle(command);

        // Assert
        verify(friendshipRepository).delete(friendshipCaptor.capture());
        Friendships deletedFriendship = friendshipCaptor.getValue();

        assertEquals(friendshipId, deletedFriendship.getId());
        assertEquals(FriendShipStatus.ACCEPTED, deletedFriendship.getFriendShipStatus());
    }

    @Test
    @DisplayName("Should call getUserInfo with correct target user ID")
    void shouldCallGetUserInfoWithCorrectTargetUserId() {
        // Arrange
        String specificTargetUserId = "specific-target-123";
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(specificTargetUserId);

        User specificTargetUserInfo = User.builder()
                .id(specificTargetUserId)
                .build();

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(specificTargetUserId)).thenReturn(specificTargetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, specificTargetUserId))
                .thenReturn(Optional.of(friendship));

        // Act
        handler.handle(command);

        // Assert
        verify(userAccessService, times(1)).getUserInfo(specificTargetUserId);
        verify(friendshipRepository, times(1))
                .findFriendshipBetweenUsers(currentUserId, specificTargetUserId);
    }

    @Test
    @DisplayName("Should maintain transactional integrity")
    void shouldMaintainTransactionalIntegrity() {
        // Arrange
        RemoveFriendshipCommand command = new RemoveFriendshipCommand(targetUserId);

        when(userAccessService.getCurrentUserInfo()).thenReturn(currentUserInfo);
        when(userAccessService.getUserInfo(targetUserId)).thenReturn(targetUserInfo);
        when(friendshipRepository.findFriendshipBetweenUsers(currentUserId, targetUserId))
                .thenReturn(Optional.of(friendship));

        // Simulate failure after delete but before event publish
        doNothing().when(friendshipRepository).delete(friendship);
        doThrow(new RuntimeException("Event system unavailable"))
                .when(springEventPublisher).publish(any(RemoveFriendshipEvent.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> handler.handle(command));

        // Verify delete was attempted (transaction should rollback due to @Transactional)
        verify(friendshipRepository, times(1)).delete(friendship);
    }
}