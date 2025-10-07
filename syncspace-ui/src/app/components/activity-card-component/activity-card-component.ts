
import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import {interval, Subject, takeUntil} from 'rxjs';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';
import { RoomState } from '../../models/api.model';
import { WebsocketService } from '../../services/websocket.service';

@Component({
  selector: 'app-activity-card-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './activity-card-component.html',
  styleUrl: './activity-card-component.css'
})
export class ActivityCardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();

  activities: WritableSignal<RoomState[]> = signal([]);
  loading: boolean = false;

  constructor(private apiService: ApiService,private websockets:WebsocketService) {}

  ngOnInit(): void {
    this.loadActivities();
    this.startActivityPolling();
  }

  startActivityPolling() {
    interval(5000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadActivities();
      });
  }

  loadActivities(): void {
    //this.loading = true;
    this.apiService.getUserActivity()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.activities.set(response.data)
          //this.loading = false;
        },
        error: (error) => {
          console.error('Error loading activities:', error);
          this.loading = false;
        }
      });
  }

  onActivityClick(activity: RoomState): void {
    // Handle click - navigate to room or show details
    console.log('Activity clicked:', activity);
  }

  getVisibleViewers(activity: RoomState): any[] {
    return activity.viewers?.slice(0, 4) || [];
  }

  getExtraViewersCount(activity: RoomState): number {
    const total = activity.viewers?.length || 0;
    return total > 4 ? total - 4 : 0;
  }

  getInitials(username: string): string {
    return username?.charAt(0).toUpperCase() || '?';
  }

  getAvatarColor(username: string): string {
    const colors = [
      'from-purple-500 to-indigo-600',
      'from-emerald-500 to-green-600',
      'from-blue-500 to-cyan-600',
      'from-red-500 to-pink-600',
      'from-yellow-500 to-orange-600',
      'from-teal-500 to-cyan-600',
      'from-indigo-500 to-purple-600',
      'from-pink-500 to-rose-600'
    ];
    const index = username?.charCodeAt(0) % colors.length || 0;
    return colors[index];
  }

  formatDuration(seconds: number): string {
    const hours = Math.floor(seconds / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;

    if (hours > 0) {
      return `${hours}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
