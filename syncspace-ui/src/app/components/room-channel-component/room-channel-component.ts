import { Component, ElementRef, ViewChild, signal, effect, OnInit, OnDestroy, WritableSignal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { YouTubePlayerModule, YouTubePlayer } from '@angular/youtube-player';
import { ApiResponse, RoomState, UserDto, VideoControlEvent } from '../../models/api.model';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, filter, Subject, switchMap, take, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { WebsocketService } from '../../services/websocket.service';
import { AuthService } from '../../services/auth.service';
import { HttpClient } from '@angular/common/http';
import { RoomConnectionEvent } from '../../services/room-connection-event';

interface Message {
  id: string;
  content: string;
  sender: {
    id: string;
    username: string;
    avatarUrl?: string;
  };
  sentAt: string;
  isOwnMessage: boolean;
}

interface ChatState {
  channelId: string;
  messages: Message[];
}

@Component({
  selector: 'app-room-channel-component',
  imports: [CommonModule, FormsModule, YouTubePlayerModule],
  templateUrl: './room-channel-component.html',
  styleUrl: './room-channel-component.css'
})
export class RoomChannelComponent implements OnInit, OnDestroy {
  
  @ViewChild('youtubePlayer') youtubePlayer?: YouTubePlayer;
  @ViewChild('chatContainer') chatContainer?: ElementRef<HTMLDivElement>;
  @ViewChild('videoUrlInput') videoUrlInput?: ElementRef<HTMLInputElement>;

  currentVideoId = signal<string>('');
  videoTitle = signal<string>('');
  videoHost = signal<string>('Unknown');
  viewerCount = signal<number>(1);
  isPlaying = signal<boolean>(false);
  isSyncing = signal<boolean>(false);
  isLoadingVideo = signal<boolean>(false);
  isLoadingMessages = signal<boolean>(false);
  isVideoCollapsed = signal<boolean>(false);
  messages = signal<ChatState>({ channelId: '', messages: [] });
  
  private destroy$ = new Subject<void>();
  private isConnected$ = new BehaviorSubject<boolean>(false);
  
  private playerReady = false;
  private playerReadyPromise?: Promise<void>;
  private playerReadyResolver?: () => void;
  private localStop = false;
  private lastSeekTime = 0;
  private seekDebounceTimeout?: any;
  private syncCheckInterval?: any;
  
  private readonly SYNC_THRESHOLD = 2.5;
  private readonly SYNC_CHECK_INTERVAL = 15000;
  
  private currentState: RoomState = {
    roomId: "",
    videoUrl: "",
    currentTimestamp: 0,
    isPlaying: false,
    lastUpdatedAt: "",
    playbackRate: 1,
  };
  
  private clientReceivedAt: number = 0;

  videoUrl: string = '';
  messageText: string = '';
  channelId: string = '';

  playerHeight: number | undefined;
  playerWidth: number | undefined;
  playerVars = {
    autoplay: 1,
    controls: 1,
    modestbranding: 1,
    rel: 0,
    showinfo: 0,
    fs: 1,
    playsinline: 1,
    origin: window.location.origin
  };

  private currentUser: WritableSignal<UserDto | null> = signal(null);

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    private tostr: ToastrService,
    private websocketService: WebsocketService,
    private authService: AuthService,
    private http: HttpClient,
    private roomConnectionEvent:RoomConnectionEvent
  ) {
    effect(() => {
      const msgs = this.messages();
      if (msgs.messages.length > 0) {
        setTimeout(() => this.scrollChatToBottom(), 100);
      }
    });
  }

  ngOnInit(): void {
  this.loadYouTubeApi();
  this.loadCurrentUser();
  
  this.route.paramMap
    .pipe(takeUntil(this.destroy$))
    .subscribe(params => {
      let roomId = params.get("roomId");
      if (!roomId?.trim()) return;

      if (this.channelId && this.channelId !== roomId) {
        this.cleanupCurrentRoom();
      }

      this.channelId = roomId;
      
      // Wait for WebSocket to be connected before subscribing
      this.websocketService.connected$
        .pipe(
          takeUntil(this.destroy$),
          filter(connected => connected),
          take(1) // Only take the first connected event
        )
        .subscribe(() => {
          console.log('[Room] WebSocket connected, setting up viewer subscriptions');
          
          // Now subscribe to viewer state events
          this.websocketService.supscribeToRoomViewersState(roomId);
          
          // Subscribe to connect/disconnect events
          this.websocketService.getRoomConnectEvents(roomId)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
              console.log('[Room] Connect event received, loading viewer count');
              this.loadViewersCount();
            });

          this.websocketService.getRoomDisconnectEvents(roomId)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
              console.log('[Room] Disconnect event received, loading viewer count');
              this.loadViewersCount();
            });
        });
      
      // Connect to the room
      this.connectToRoom(this.channelId);
    });

  this.loadInitialData();
  this.startPeriodicSyncCheck();
}

  loadViewersCount(): void {
    if (!this.channelId.trim()) return;
    
    this.apiService.getRoomViewers(this.channelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: ApiResponse<any[]>) => {
          const count = response.data.length;
          this.viewerCount.set(count);
        },
        error: (error) => {
          console.error('[Room] Error loading viewer count:', error);
        }
      });
  }

  ngOnDestroy(): void {
    const roomId = this.channelId;
    
    this.clearSyncCheckInterval();
    clearTimeout(this.seekDebounceTimeout);
    
    if (this.isHost() && this.hasActiveVideo()) {
      this.stopVideoOnLeave();
    }
    
    if (this.channelId) {
      this.websocketService.stopViewing(this.channelId);
    }
    
    if (roomId) {
      this.apiService.disconnectToRoom(roomId)
        .pipe(take(1))
        .subscribe({
          next: (response) => {
            console.log('[Room] Disconnected on destroy:', response.message);
            this.roomConnectionEvent.notifyRoomConnection();
          },
          error: (error) => {
            console.error('[Room] Error disconnecting on destroy:', error);
          }
        });
    }
    
    this.destroy$.next();
    this.destroy$.complete();
  }

  private cleanupCurrentRoom(): void {
    const roomId = this.channelId;
    
    if (roomId) {
      if (confirm("Are you sure you want to leave?")) {
        this.apiService.disconnectToRoom(roomId)
          .pipe(take(1))
          .subscribe({
            next: (response) => {
              this.tostr.success(response.message);
              this.performCleanup();
              this.roomConnectionEvent.notifyRoomConnection();
            },
            error: (error) => {
              console.error('[Room] Error disconnecting:', error);
              this.tostr.error('Failed to disconnect properly');
            }
          });
      } else {
        return;
      }
    } else {
      this.performCleanup();
    }
  }

  private performCleanup(): void {
    this.clearSyncCheckInterval();
    clearTimeout(this.seekDebounceTimeout);
    
    if (this.isHost() && this.hasActiveVideo()) {
      this.stopVideoOnLeave();
    }
    
    if (this.channelId) {
      this.websocketService.stopViewing(this.channelId);
    }
    
    this.resetVideoState();
    this.playerReady = false;
    this.playerReadyPromise = undefined;
    this.playerReadyResolver = undefined;
    this.localStop = false;
    this.lastSeekTime = 0;
    this.clientReceivedAt = 0;
    this.isConnected$.next(false);
    
    this.isPlaying.set(false);
    this.isSyncing.set(false);
    this.isLoadingVideo.set(false);
    this.viewerCount.set(1);
    
    this.messages.set({ channelId: '', messages: [] });
    
    this.videoUrl = '';
    this.messageText = '';
  }

  private loadCurrentUser(): void {
    this.currentUser.set(this.authService.getCurrentUser());
  }

  private startPeriodicSyncCheck(): void {
    this.syncCheckInterval = setInterval(() => {
      if (this.playerReady && this.currentVideoId() && !this.isHost()) {
        this.performSyncCheck();
      }
    }, this.SYNC_CHECK_INTERVAL);
  }

  private clearSyncCheckInterval(): void {
    if (this.syncCheckInterval) {
      clearInterval(this.syncCheckInterval);
      this.syncCheckInterval = null;
    }
  }

  private async performSyncCheck(): Promise<void> {
    try {
      const currentTime = await this.getCurrentTime();
      const expectedTime = this.calculateCurrentTimestamp(this.currentState);
      const drift = Math.abs(currentTime - expectedTime);

      if (drift > this.SYNC_THRESHOLD) {
        await this.synchronizePlayer(this.currentState);
      }
    } catch (error) {
      console.error('[SyncCheck] Error during sync check:', error);
    }
  }

  connectToRoom(channelId: string): void {
    this.apiService.fetchRoomState(channelId)
      .pipe(
        takeUntil(this.destroy$),
        switchMap((stateResponse: ApiResponse<RoomState>) => {
          let data = stateResponse.data;
          this.clientReceivedAt = Date.now();
          
          this.currentState = data;
          
          if (data.videoUrl?.trim()) {
            this.applyToVideo(data);
          }
          
          return this.apiService.connectToRoom(this.channelId);
        })
      )
      .subscribe({
        next: (connectResponse: ApiResponse<any>) => {
          this.tostr.success(connectResponse.message);
          
          this.subscribeToRoomUpdates(this.currentState.roomId);
          this.isConnected$.next(true);
          this.roomConnectionEvent.notifyRoomConnection();
          
          this.websocketService.getRoomResetEvent(this.currentState.roomId)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
              if (this.localStop) {
                this.localStop = false;
                return;
              }
              this.resetVideoState();
            });
          
          setTimeout(() => {
            this.loadViewersCount();
          }, 300);
        },
        error: (error) => {
          console.error('[RoomService] Error connecting to room:', error);
          this.tostr.error('Failed to connect to room');
        }
      });
  }

  private subscribeToRoomUpdates(channelId: string): void {
    this.websocketService.subscribeToRoom(channelId);

    this.websocketService.getRoomControlEvents(channelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe(event => {
        this.handleRemoteEvent(event);
      });
  }

  private async handleRemoteEvent(event: VideoControlEvent): Promise<void> {
    this.clientReceivedAt = Date.now();
    
    const updatedState = { ...this.currentState };

    switch (event.action) {
      case 'PLAY':
        updatedState.isPlaying = true;
        updatedState.currentTimestamp = event.timestamp || this.currentState.currentTimestamp;
        updatedState.lastUpdatedAt = new Date().toISOString();
        break;

      case 'PAUSE':
        updatedState.isPlaying = false;
        updatedState.currentTimestamp = event.timestamp || this.currentState.currentTimestamp;
        updatedState.lastUpdatedAt = new Date().toISOString();
        break;

      case 'SEEK':
        updatedState.currentTimestamp = event.timestamp!;
        updatedState.lastUpdatedAt = new Date().toISOString();
        break;

      case 'CHANGE_VIDEO':
        updatedState.videoUrl = event.videoUrl!;
        updatedState.currentTimestamp = 0;
        updatedState.isPlaying = false;
        updatedState.lastUpdatedAt = new Date().toISOString();
        updatedState.hoster = event.user;
        this.addSystemMessage(`${event.user.username} started playing a video`);
        break;
    }
    
    this.currentState = updatedState;
    await this.applyToVideo(updatedState);
  }

  async applyToVideo(state: RoomState): Promise<void> {
    const videoId = this.extractVideoId(state.videoUrl);
    if (!videoId) {
      this.tostr.error('Invalid YouTube URL or Video ID');
      return;
    }

    const isNewVideo = this.currentVideoId() !== videoId;
    
    this.currentState = state;
    
    if (isNewVideo) {
      this.isLoadingVideo.set(true);
      this.playerReady = false;
      this.currentVideoId.set(videoId);
      this.videoTitle.set(state.videoTitle!);
      this.videoHost.set(state.hoster?.username || 'Unknown');
      this.isVideoCollapsed.set(false);
      
      this.playerReadyPromise = new Promise(resolve => {
        this.playerReadyResolver = resolve;
      });
      
      try {
        await this.playerReadyPromise;
        this.isLoadingVideo.set(false);
      } catch (error) {
        console.error('[RoomService] Player ready timeout:', error);
        this.isLoadingVideo.set(false);
        return;
      }
    } else {
      await this.synchronizePlayer(state);
    }
  }

  private async synchronizePlayer(state: RoomState): Promise<void> {
    if (!this.playerReady || !this.youtubePlayer) {
      return;
    }

    this.isSyncing.set(true);
    
    try {
      const actualTimestamp = this.calculateCurrentTimestamp(state);

      const currentTime = await this.getCurrentTime();
      const playerState = await this.youtubePlayer.getPlayerState();
      const isCurrentlyPlaying = playerState === 1;
      const isBuffering = playerState === 3;

      if (isBuffering) {
        this.isSyncing.set(false);
        return;
      }

      const drift = Math.abs(currentTime - actualTimestamp);

      if (drift > this.SYNC_THRESHOLD) {
        await this.seekTo(actualTimestamp);
        await this.sleep(500);
      }

      if (state.isPlaying && !isCurrentlyPlaying) {
        await this.playVideoElement();
      } else if (!state.isPlaying && isCurrentlyPlaying) {
        await this.pauseVideoElement();
      }

      if (state.playbackRate) {
        const currentRate = await this.getPlaybackRate();
        if (Math.abs(currentRate - state.playbackRate) > 0.01) {
          await this.setPlaybackRate(state.playbackRate);
        }
      }

      this.isLoadingVideo.set(false);
      
    } catch (error) {
      console.error('[RoomService] Error synchronizing player:', error);
      this.isLoadingVideo.set(false);
    } finally {
      await this.sleep(1000);
      this.isSyncing.set(false);
    }
  }

  private sleep(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  private async getCurrentTime(): Promise<number> {
    if (!this.youtubePlayer) return 0;
    try {
      return await this.youtubePlayer.getCurrentTime();
    } catch (e) {
      console.error('[RoomService] Error getting current time:', e);
      return 0;
    }
  }

  private async seekTo(timestamp: number): Promise<void> {
    if (this.youtubePlayer) {
      try {
        await this.youtubePlayer.seekTo(timestamp, true);
      } catch (e) {
        console.error('[RoomService] Error seeking:', e);
      }
    }
  }

  private async playVideoElement(): Promise<void> {
    if (this.youtubePlayer) {
      try {
        await this.youtubePlayer.playVideo();
      } catch (e) {
        console.error('[RoomService] Error playing video:', e);
      }
    }
  }

  private async pauseVideoElement(): Promise<void> {
    if (this.youtubePlayer) {
      try {
        await this.youtubePlayer.pauseVideo();
      } catch (e) {
        console.error('[RoomService] Error pausing video:', e);
      }
    }
  }

  private async setPlaybackRate(rate: number): Promise<void> {
    if (this.youtubePlayer) {
      try {
        await this.youtubePlayer.setPlaybackRate(rate);
      } catch (e) {
        console.error('[RoomService] Error setting playback rate:', e);
      }
    }
  }

  private async getPlaybackRate(): Promise<number> {
    try {
      return await (this.youtubePlayer?.getPlaybackRate() ?? 1);
    } catch (e) {
      console.error('[RoomService] Error getting playback rate:', e);
      return 1;
    }
  }

  private calculateCurrentTimestamp(state: RoomState): number {
    if (!state.isPlaying) {
      return state.currentTimestamp;
    }

    const now = Date.now();
    const elapsedSeconds = (now - this.clientReceivedAt) / 1000;
    
    if (elapsedSeconds < 0 || elapsedSeconds > 86400) {
      console.warn('[calculateTimestamp] Invalid elapsed time:', elapsedSeconds, 'seconds. Using base timestamp.');
      return state.currentTimestamp;
    }
    
    const playbackRate = state.playbackRate || 1.0;
    const calculated = state.currentTimestamp + (elapsedSeconds * playbackRate);
    
    return Math.max(0, calculated);
  }

  private loadYouTubeApi(): void {
    if (!(window as any).YT) {
      const tag = document.createElement('script');
      tag.src = 'https://www.youtube.com/iframe_api';
      document.body.appendChild(tag);
    }
  }

  private loadInitialData(): void {
    this.isLoadingMessages.set(true);
    this.messages.set({
      channelId: this.channelId,
      messages: [
        {
          id: '1',
          content: 'Welcome to the watch party! 🎉',
          sender: {
            id: 'system',
            username: 'System',
            avatarUrl: undefined
          },
          sentAt: this.formatTime(new Date()),
          isOwnMessage: false
        }
      ]
    });
    this.isLoadingMessages.set(false);
  }

  private extractVideoId(url: string): string | null {
    if (!url) return null;

    if (url.length === 11 && /^[a-zA-Z0-9_-]+$/.test(url)) {
      return url;
    }

    const patterns = [
      /(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([a-zA-Z0-9_-]{11})/,
      /youtube\.com\/watch\?.*v=([a-zA-Z0-9_-]{11})/
    ];

    for (const pattern of patterns) {
      const match = url.match(pattern);
      if (match && match[1]) {
        return match[1];
      }
    }

    return null;
  }

  loadVideo(): void {
    const videoId = this.extractVideoId(this.videoUrl);
    
    if (!videoId) {
      this.tostr.error('Invalid YouTube URL or Video ID');
      return;
    }

    if (!this.currentUser()?.id) {
      this.tostr.error('User not authenticated');
      return;
    }

    const event = {
      channelId: this.channelId,
      action: 'CHANGE_VIDEO',
      timestamp: 0,
      videoUrl: videoId,
      userId: this.currentUser()?.id!
    };

    this.isLoadingVideo.set(true);

    this.apiService.sendControlEvent(event)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (res) => {
          this.tostr.success(res.message);
          this.videoUrl = '';
        },
        error: (error) => {
          console.error('[LoadVideo] Error:', error);
          this.tostr.error('Failed to load video');
          this.isLoadingVideo.set(false);
        }
      });
  }

  private fetchVideoDetails(videoId: string): void {
    const url = `https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=${videoId}&format=json`;

    this.http.get(url, { responseType: 'text' })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data: any) => {
          try {
            const parsed = JSON.parse(data);
            this.videoTitle.set(parsed.title);
          } catch {
            this.videoTitle.set('Unknown Video');
          }
        },
        error: (err) => {
          console.error(err);
          this.videoTitle.set('Unknown Video');
        }
      });
  }

  async onPlayerReady(event: any) {
    this.playerReady = true;
    
    const correctTimestamp = this.calculateCurrentTimestamp(this.currentState);
    
    if (this.currentState.videoUrl) {
      try {
        await this.sleep(500);
        
        await this.youtubePlayer?.seekTo(correctTimestamp, true);
        await this.sleep(200);
        
        let currentTime = await this.getCurrentTime();
        
        if (Math.abs(currentTime - correctTimestamp) > 1) {
          await this.youtubePlayer?.seekTo(correctTimestamp, true);
          await this.sleep(300);
          currentTime = await this.getCurrentTime();
        }
        
        if (this.currentState.isPlaying) {
          await this.playVideoElement();
          
          await this.sleep(500);
          const finalTime = await this.getCurrentTime();
          const expectedTime = this.calculateCurrentTimestamp(this.currentState);
          const drift = Math.abs(finalTime - expectedTime);
          
          if (drift > 2) {
            await this.youtubePlayer?.seekTo(expectedTime, true);
          }
        } else {
          await this.pauseVideoElement();
        }
      } catch (error) {
        console.error('[Player] Error setting initial position:', error);
      }
    }
    
    if (this.playerReadyResolver) {
      this.playerReadyResolver();
      this.playerReadyResolver = undefined;
    }
  }

  onPlayerStateChange(event: any): void {
    const state = event.data;
    const isHost = this.isHost();
    
    this.isPlaying.set(state === 1);

    if (this.isSyncing() || !isHost) {
      return;
    }

    if (state === 0) {
      if (this.isHost() && this.hasActiveVideo()) {
        this.stopVideoOnLeave();
        this.addSystemMessage('Video ended');
        this.resetVideoState();
      }
    } else if (state === 2) {
      this.handleUserPause();
    } else if (state === 1) {
      this.handleUserPlay();
    }
  }

  onPlayerError(event: any): void {
    const errorCode = event.data;
    let errorMessage = 'Video playback error';
    
    switch (errorCode) {
      case 2:
        errorMessage = 'Invalid video ID';
        break;
      case 5:
        errorMessage = 'HTML5 player error';
        break;
      case 100:
        errorMessage = 'Video not found';
        break;
      case 101:
      case 150:
        errorMessage = 'Video cannot be embedded or is restricted';
        break;
    }
    
    console.error('[Player] Error:', errorCode, errorMessage);
    this.tostr.error(errorMessage);
    this.isLoadingVideo.set(false);
    this.isSyncing.set(false);
    
    if (this.isHost()) {
      setTimeout(() => this.stopVideo(), 2000);
    }
  }

  private async handleUserPause(): Promise<void> {
    if (!this.isHost()) return;
    
    try {
      const currentTime = await this.getCurrentTime();
      await this.sendControlEvent('PAUSE', currentTime);
      this.addSystemMessage('Video paused');
    } catch (error) {
      console.error('[Player] Error handling pause:', error);
    }
  }

  private async handleUserPlay(): Promise<void> {
    if (!this.isHost()) return;
    
    try {
      const currentTime = await this.getCurrentTime();
      await this.sendControlEvent('PLAY', currentTime);
    } catch (error) {
      console.error('[Player] Error handling play:', error);
    }
  }

  private async sendControlEvent(action: string, timestamp?: number): Promise<void> {
    if (!this.currentUser()?.id) return;
    
    const event = {
      channelId: this.channelId,
      action,
      timestamp: timestamp ?? 0,
      videoUrl: this.currentState.videoUrl,
      userId: this.currentUser()?.id!
    };
    
    this.apiService.sendControlEvent(event)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => console.log('[Control] Event sent:', action),
        error: (error) => console.error('[Control] Error sending event:', error)
      });
  }

  togglePlayPause(): void {
    if (!this.youtubePlayer || !this.isHost()) return;

    if (this.isPlaying()) {
      this.youtubePlayer.pauseVideo();
    } else {
      this.youtubePlayer.playVideo();
    }
  }

  async skipForward(): Promise<void> {
    if (!this.youtubePlayer || !this.isHost()) return;
    
    try {
      const currentTime = await this.getCurrentTime();
      const newTime = currentTime + 10;
      await this.seekTo(newTime);
      await this.sendControlEvent('SEEK', newTime);
    } catch (error) {
      console.error('[Player] Error skipping forward:', error);
    }
  }

  stopVideo(): void {
    if (!this.isHost()) {
      this.tostr.warning('Only the host can stop the video');
      return;
    }
    
    if (confirm('Stop and remove the current video?')) {
      this.localStop = true;
      
      this.apiService.resetRoomState(this.channelId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.resetVideoState();
          },
          error: (error) => {
            console.error('[Stop] Error stopping video:', error);
            this.tostr.error('Failed to stop video');
            this.localStop = false;
          }
        });
    }
  }

  private stopVideoOnLeave(): void {
    this.localStop = true;
    
    this.apiService.resetRoomState(this.channelId)
      .subscribe({
        next: () => {
          console.log('[Stop] Video stopped on host leave');
        },
        error: (error) => {
          console.error('[Stop] Error stopping video on leave:', error);
        }
      });
  }

  private resetVideoState(): void {
    this.currentVideoId.set('');
    this.videoTitle.set('');
    this.isPlaying.set(false);
    this.isVideoCollapsed.set(false);
    this.playerReady = false;
    this.currentState = {
      roomId: this.channelId,
      videoUrl: "",
      currentTimestamp: 0,
      isPlaying: false,
      lastUpdatedAt: "",
      playbackRate: 1,
    };
    this.addSystemMessage('Video stopped');
  }

  private addSystemMessage(content: string): void {
    const systemMessage: Message = {
      id: `system-${Date.now()}`,
      content,
      sender: {
        id: 'system',
        username: 'System',
        avatarUrl: undefined
      },
      sentAt: this.formatTime(new Date()),
      isOwnMessage: false
    };

    const currentMessages = this.messages();
    this.messages.set({
      ...currentMessages,
      messages: [...currentMessages.messages, systemMessage]
    });
  }

  toggleVideoCollapse(): void {
    this.isVideoCollapsed.set(!this.isVideoCollapsed());
  }

  scrollToInput(): void {
    this.videoUrlInput?.nativeElement.focus();
    this.videoUrlInput?.nativeElement.scrollIntoView({
      behavior: 'smooth',
      block: 'center'
    });
  }

  private scrollChatToBottom(): void {
    if (this.chatContainer) {
      const element = this.chatContainer.nativeElement;
      element.scrollTop = element.scrollHeight;
    }
  }

  private formatTime(date: Date): string {
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
  }

  trackByMessageId(index: number, message: Message): string {
    return message.id;
  }

  isHost(): boolean {
    return this.currentUser()?.id === this.currentState.hoster?.id;
  }

  hasActiveVideo(): boolean {
    return this.currentVideoId() !== '';
  }
}