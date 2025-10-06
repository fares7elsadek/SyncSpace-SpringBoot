import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { FriendshipDto } from '../../models/api.model';
import { WebsocketService } from '../../services/websocket.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-all-friends-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './all-friends-component.html',
  styleUrl: './all-friends-component.css'
})
export class AllFriendsComponent implements OnInit, OnDestroy {
  friends: WritableSignal<FriendshipDto[]> = signal([]);
  activeFriendOptions = signal<string>('');

  private destroy$ = new Subject<void>();

  constructor(
    private apiService: ApiService,
    private toastr: ToastrService,
    private websocketService: WebsocketService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
  this.loadFriends();

  this.websocketService.userPresence$  
    .pipe(takeUntil(this.destroy$))
    .subscribe((message) => {
      this.updateFriendOnlineStatus(message.userId, message.status);
    });
}


  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadFriends(): void {
    this.apiService.getFriends().pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.friends.set(response.data);
      },
      error: (err) => {
        this.toastr.error(err.error?.error || 'Failed to load friends');
      }
    });
  }

  toggleOptions(userId: string, event: MouseEvent): void {
    event.stopPropagation();
    this.activeFriendOptions.set(
      this.activeFriendOptions() === userId ? '' : userId
    );
  }

  closeOptions(): void {
    this.activeFriendOptions.set('');
  }

  startDM(userId: string): void {
    // TODO: navigate to DM page with this user
    console.log('Start DM with:', userId);
  }

  showFriendOptions(userId: string): void {
    this.toggleOptions(userId, new MouseEvent('click'));
  }

  removeFriend(userId: string): void {
    this.apiService
      .removeFriend(userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastr.success('Friend removed successfully');
          this.loadFriends();
        },
        error: (err) => {
          this.toastr.error(err.error?.error || 'Failed to remove friend');
        }
      });
  }

  updateFriendOnlineStatus(friendId: string, status: string): void {
    this.friends.update((friendsList) =>
      friendsList.map((friend) =>
        friend.user.id === friendId
          ? {
              ...friend,
              user: { ...friend.user, isOnline: this.getStatus(status) }
            }
          : friend
      )
    );
  }

  getStatus(status: string): boolean {
    return status === 'ONLINE';
  }

  friendStatus(online: boolean): string {
    return online ? 'online' : 'offline';
  }
}
