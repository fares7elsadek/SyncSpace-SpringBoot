import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { Subject, takeUntil, finalize } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { NotificationDto, FriendshipDto } from '../../models/api.model';
import { FriendAcceptedEvent } from '../../services/friend-accepted-event';
import { NotificationIncomingEvent } from '../../services/notification-incoming-event';

interface Notification {
  id: string;
  type: 'DIRECT_MESSAGE' | 'FRIEND_REQUEST' | 'FRIEND_ACCEPTED' | 'SYSTEM' | "GROUP_MESSAGE";
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  avatar?: string;
  actionable?: boolean;
  relatedEntityId: string;
  friendshipStatus?: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  username?: string;
}

@Component({
  selector: 'app-top-bar-friend-component',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './top-bar-friend-component.html',
  styleUrl: './top-bar-friend-component.css'
})
export class TopBarFriendComponent implements OnInit, OnDestroy {

  showAddFriendModal = signal(false);
  showNotificationsModal = signal(false);
  isSendingRequest = signal(false);
  errorMessage = signal("");
  isLoadingNotifications = signal(false);
  hasMoreNotifications = signal(true);
  isLoadingMore = signal(false);
  
  username: string = "";
  notifications = signal<Notification[]>([]);
  unreadCount = signal(0);
  page = 0;
  size = 20;
  totalElements = 0;

  private destroy$ = new Subject<void>();

  constructor(
    private router: Router, 
    private apiService: ApiService, 
    private toastr: ToastrService,
    private friendAcceptedEvent:FriendAcceptedEvent,
    private notificationEvent:NotificationIncomingEvent
  ) {}

  ngOnInit(): void {
    this.loadNotifications(true);
    this.notificationEvent.notification$
    .pipe(takeUntil(this.destroy$))
    .subscribe(() => this.loadNotifications(true))
    
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  isActive(route: string): boolean {
    return this.router.url.startsWith(route);
  }

  // Friend Request Methods
  sendFriendRequest() {
    if (!this.username.trim()) return;
    
    this.isSendingRequest.set(true);
    this.errorMessage.set("");
    
    this.apiService.sendFriendRequest(this.username.trim())
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.isSendingRequest.set(false))
      )
      .subscribe({
        next: () => {
          this.toastr.success("Friend request sent successfully");
          this.closeAddFriendModal();
        },
        error: (err) => {
          const errorMsg = err?.error?.error || err?.message || 'An error occurred while sending the request';
          this.errorMessage.set(errorMsg);
        }
      });
  }

  closeAddFriendModal() {
    this.showAddFriendModal.set(false);
    this.isSendingRequest.set(false);
    this.username = "";
    this.errorMessage.set("");
  }

  // Notification Methods
  loadNotifications(reset: boolean = false) {
    if (this.isLoadingNotifications() || this.isLoadingMore()) return;
    
    if (reset) {
      this.page = 0;
      this.notifications.set([]);
      this.hasMoreNotifications.set(true);
      this.isLoadingNotifications.set(true);
    } else {
      this.isLoadingMore.set(true);
    }

    this.apiService.getNotifications(this.page, this.size)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoadingNotifications.set(false);
          this.isLoadingMore.set(false);
        })
      )
      .subscribe({
        next: (response) => {
          console.log('Notifications response:', response);
          
          if (response?.data && Array.isArray(response.data)) {
            const newNotifications: Notification[] = [];
            
            // Process only friendship-related notifications
            const friendshipNotifications = response.data.filter(ntf => 
              ntf.type === 'FRIEND_REQUEST' || ntf.type === 'FRIEND_ACCEPTED'
            );
            
            // Process each notification
            const processPromises = friendshipNotifications.map(ntf => 
              this.processNotificationAsync(ntf)
            );
            
            Promise.all(processPromises).then(processedNotifications => {
              const validNotifications = processedNotifications.filter(n => n !== null) as Notification[];
              
              if (reset) {
                this.notifications.set(validNotifications);
              } else {
                this.notifications.update(current => [...current, ...validNotifications]);
              }
              
              // Update pagination info
              this.totalElements = Number(response.message) || 0;
              const totalPages = Math.ceil(this.totalElements / this.size);
              this.hasMoreNotifications.set(this.page < totalPages - 1);
              
              this.updateUnreadCount();
            });
          } else {
            this.hasMoreNotifications.set(false);
          }
        },
        error: (err) => {
          console.error('Error loading notifications:', err);
          this.toastr.error('Failed to load notifications');
        }
      });
  }

  loadMoreNotifications() {
    if (this.hasMoreNotifications() && !this.isLoadingMore()) {
      this.page++;
      this.loadNotifications(false);
    }
  }

  private async processNotificationAsync(ntf: NotificationDto): Promise<Notification | null> {
    try {
      if (ntf.type === 'FRIEND_REQUEST' || ntf.type === 'FRIEND_ACCEPTED') {
        return await this.handleFriendshipNotification(ntf);
      }
      return null;
    } catch (error) {
      console.error('Error processing notification:', error);
      return this.createDefaultNotification(ntf);
    }
  }

  private handleFriendshipNotification(ntf: NotificationDto): Promise<Notification | null> {
    if (!ntf.relatedEntityId) {
      console.warn('No related entity ID for friendship notification');
      return Promise.resolve(this.createDefaultNotification(ntf));
    }

    return new Promise((resolve) => {
      this.apiService.getFriend(ntf.relatedEntityId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            if (response?.data) {
              const friendship: FriendshipDto = response.data;
              const notification = this.createFriendshipNotification(ntf, friendship);
              resolve(notification);
            } else {
              resolve(this.createDefaultNotification(ntf));
            }
          },
          error: (err) => {
            console.error('Error fetching friendship details:', err);
            resolve(this.createDefaultNotification(ntf));
          }
        });
    });
  }

  private createFriendshipNotification(ntf: NotificationDto, friendship: FriendshipDto): Notification {
    const username = friendship.user?.username || 'Unknown User';
    let title: string;
    let message: string;
    let actionable = false;

    switch (ntf.type) {
      case 'FRIEND_REQUEST':
        if (friendship.friendShipStatus === 'PENDING') {
          title = 'Friend Request';
          message = `${username} wants to connect with you.`;
          actionable = true;
        } else if (friendship.friendShipStatus === 'ACCEPTED') {
          title = 'Friend Request Accepted';
          message = `You and ${username} are now friends!`;
          actionable = false;
        } else {
          title = 'Friend Request';
          message = `Friend request from ${username}.`;
          actionable = false;
        }
        break;
      case 'FRIEND_ACCEPTED':
        title = 'Friend Request Accepted';
        message = `${username} accepted your friend request. You are now friends!`;
        actionable = false;
        break;
      default:
        title = 'Friendship Update';
        message = `Update from ${username}`;
        actionable = false;
    }

    return {
      id: ntf.id,
      type: ntf.type,
      title,
      message,
      timestamp: ntf.createdAt,
      read: ntf.read,
      avatar: friendship.user?.avatarUrl,
      actionable,
      relatedEntityId: friendship.id,
      friendshipStatus: friendship.friendShipStatus,
      username
    };
  }

  private createDefaultNotification(ntf: NotificationDto): Notification {
    return {
      id: ntf.id,
      type: ntf.type,
      title: 'System Notification',
      message: 'A notification was received.',
      timestamp: ntf.createdAt,
      read: ntf.read,
      actionable: false,
      relatedEntityId: ntf.relatedEntityId || ''
    };
  }


  updateUnreadCount() {
    const unread = this.notifications().filter(n => !n.read).length;
    this.unreadCount.set(unread);
  }

  markAsRead(notificationId: string) {
    // Optimistically update UI
    this.notifications.update(current => 
      current.map(n => 
        n.id === notificationId ? { ...n, read: true } : n
      )
    );
    this.updateUnreadCount();

    // Update on server
    this.apiService.markNotificationAsRead(notificationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        error: (err) => {
          console.error('Error marking notification as read:', err);
          // Revert optimistic update on error
          this.notifications.update(current => 
            current.map(n => 
              n.id === notificationId ? { ...n, read: false } : n
            )
          );
          this.updateUnreadCount();
          this.toastr.error('Failed to mark notification as read');
        }
      });
  }

  markAllAsRead() {
    // Get all unread notification IDs
    const unreadIds = this.notifications().filter(n => !n.read).map(n => n.id);
    
    if (unreadIds.length === 0) return;

    // Optimistically update UI
    this.notifications.update(current => 
      current.map(n => ({ ...n, read: true }))
    );
    this.updateUnreadCount();

    // Update on server
    this.apiService.markAllNotificationsAsRead()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastr.success('All notifications marked as read');
        },
        error: (err) => {
          console.error('Error marking all notifications as read:', err);
          // Revert optimistic update on error
          this.notifications.update(current => 
            current.map(n => 
              unreadIds.includes(n.id) ? { ...n, read: false } : n
            )
          );
          this.updateUnreadCount();
          this.toastr.error('Failed to mark all notifications as read');
        }
      });
  }

  deleteNotification(notificationId: string) {
    // Optimistically remove from UI
    const notificationToDelete = this.notifications().find(n => n.id === notificationId);
    this.notifications.update(current => 
      current.filter(n => n.id !== notificationId)
    );
    this.updateUnreadCount();

    // Delete on server
    this.apiService.deleteNotification(notificationId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastr.success("Notification deleted successfully");
        },
        error: (err) => {
          console.error('Error deleting notification:', err);
          // Revert optimistic update on error
          if (notificationToDelete) {
            this.notifications.update(current => [...current, notificationToDelete]);
            this.updateUnreadCount();
          }
          this.toastr.error('Failed to delete notification');
        }
      });
  }

  acceptFriendRequest(entityId: string, notificationId: string) {
    if (!entityId) {
      this.toastr.error('Invalid friend request');
      return;
    }

    // Optimistically update the notification
    this.notifications.update(current => 
      current.map(n => 
        n.id === notificationId 
          ? { ...n, actionable: false, message: `You and ${n.username} are now friends!`, read: true }
          : n
      )
    );
    this.updateUnreadCount();

    this.apiService.acceptFriendRequest(entityId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastr.success("Friend request accepted successfully");
          // Mark the notification as read on server
          this.apiService.markNotificationAsRead(notificationId)
            .pipe(takeUntil(this.destroy$))
            .subscribe();
          this.friendAcceptedEvent.notifyAcceptedEvent();
        },
        error: (err) => {
          console.error('Error accepting friend request:', err);
          this.toastr.error('Failed to accept friend request');
          // Revert optimistic update
          this.loadNotifications(true);
        }
      });
  }

  rejectFriendRequest(entityId: string, notificationId: string) {
    if (!entityId) {
      this.toastr.error('Invalid friend request');
      return;
    }

    // Remove the notification optimistically
    this.notifications.update(current => 
      current.filter(n => n.id !== notificationId)
    );
    this.updateUnreadCount();

    this.apiService.rejectFriendRequest(entityId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastr.success("Friend request rejected");
        },
        error: (err) => {
          console.error('Error rejecting friend request:', err);
          this.toastr.error('Failed to reject friend request');
          // Reload notifications on error
          this.loadNotifications(true);
        }
      });
  }

  closeNotificationsModal() {
    this.showNotificationsModal.set(false);
  }

  toggleNotificationsModal() {
    this.showNotificationsModal.set(!this.showNotificationsModal());
    if (this.showNotificationsModal()) {
      this.loadNotifications(true);
    }
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'FRIEND_REQUEST':
      case 'FRIEND_ACCEPTED':
        return 'M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z';
      case 'GROUP_MESSAGE':
      case 'DIRECT_MESSAGE':
        return 'M20 2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h4l4 4 4-4h4c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2z';
      default:
        return 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z';
    }
  }

  clearAllNotifications() {
    if (this.notifications().length === 0) return;

    // Store current notifications for potential revert
    const currentNotifications = this.notifications();
    
    // Optimistically clear UI
    this.notifications.set([]);
    this.updateUnreadCount();
    
    // Note: You might want to implement a clearAllNotifications API endpoint
    // For now, we'll just clear locally
    this.toastr.success('All notifications cleared');
  }

  refreshNotifications() {
    this.loadNotifications(true);
  }
}