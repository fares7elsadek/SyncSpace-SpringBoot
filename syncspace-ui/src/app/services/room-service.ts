// import { Injectable, OnDestroy } from '@angular/core';
// import { RoomState, VideoControlEvent } from '../models/api.model';
// import { BehaviorSubject, Observable } from 'rxjs';
// import { WebsocketService } from './websocket.service';
// import { ApiService } from './api.service';
// import { AuthService } from './auth.service';
// import { YouTubePlayer } from '@angular/youtube-player';

// interface SyncCheckEvent {
//   channelId: string;
//   authoritativeTimestamp: number;
//   isPlaying: boolean;
//   serverTime: number;
// }

// @Injectable({
//   providedIn: 'root'
// })
// export class RoomService implements OnDestroy {

//   private roomState$ = new BehaviorSubject<RoomState | null>(null);
//   private isConnected$ = new BehaviorSubject<boolean>(false);
//   private isSyncing$ = new BehaviorSubject<boolean>(false);
  
//   private readonly SYNC_CHECK_INTERVAL = 8000; 
//   private readonly SYNC_THRESHOLD = 2.5; 
//   private readonly HARD_SYNC_THRESHOLD = 5; 
  
//   private syncCheckTimer: any;
//   private currentChannelId: string | null = null;
//   private youtubePlayer: YouTubePlayer | null = null;
//   private isLocalAction = false;

//   constructor(
//     private websocketService: WebsocketService,
//     private apiService: ApiService,
//     private authService: AuthService
//   ) {}

//   async connectToRoom(channelId: string, youtubePlayer: YouTubePlayer): Promise<void> {
//     try {
//       console.log('[RoomService] Connecting to room:', channelId);
//       this.isSyncing$.next(true);
//       this.currentChannelId = channelId;
//       this.youtubePlayer = youtubePlayer;

//       console.log('[RoomService] Fetching room state from API...');
//       const response = await this.apiService.fetchRoomState(channelId).toPromise();
      
//       console.log('[RoomService] API response:', response);
      
//       if (response?.success && response.data) {
//         const initialState = response.data;
//         console.log('[RoomService] Initial room state:', initialState);
        
//         this.roomState$.next(initialState);
        
//         // 2. Apply initial state to video player
//         console.log('[RoomService] Applying initial state to video player');
//         await this.applyStateToVideo(initialState);
        
//         // 3. Subscribe to WebSocket for real-time updates
//         console.log('[RoomService] Subscribing to room updates via WebSocket');
//         this.subscribeToRoomUpdates(channelId);
        
//         // 4. Start periodic drift correction
//         console.log('[RoomService] Starting drift monitoring');
//         //this.startDriftMonitoring();
        
//         this.isConnected$.next(true);
//         console.log('[RoomService] Successfully connected to room');
//       } else {
//         console.error('[RoomService] Invalid API response:', response);
//         throw new Error('Invalid room state response');
//       }
//     } catch (error) {
//       console.error('[RoomService] Failed to connect to room:', error);
//       throw error;
//     } finally {
//       this.isSyncing$.next(false);
//     }
//   }

//   disconnectFromRoom(): void {
//     console.log('[RoomService] Disconnecting from room');
//     //this.stopDriftMonitoring();
    
//     if (this.currentChannelId) {
//       this.websocketService.unsubscribeFromRoom(this.currentChannelId);
//     }
    
//     this.isConnected$.next(false);
//     this.currentChannelId = null;
//     this.youtubePlayer = null;
//     this.roomState$.next(null);
//   }

//   async play(timestamp: number): Promise<void> {
//     if (!this.currentChannelId || !this.canSendEvent()) {
//       console.log('[RoomService] Cannot send play event - not connected');
//       return;
//     }
    
//     console.log('[RoomService] Sending PLAY event at timestamp:', timestamp);
//     this.isLocalAction = true;
    
//     const event: VideoControlEvent = {
//       channelId: this.currentChannelId,
//       action: 'PLAY',
//       timestamp: timestamp,
//       userId: this.getCurrentUserId()
//     };

//     this.updateLocalState({
//       ...this.roomState$.value!,
//       isPlaying: true,
//       currentTimestamp: timestamp,
//       lastUpdatedAt: new Date().toISOString()
//     });

//     try {
//       await this.apiService.sendControlEvent(event).toPromise();
//       console.log('[RoomService] PLAY event sent successfully');
//     } catch (error) {
//       console.error('[RoomService] Failed to send play event:', error);
//       this.isLocalAction = false;
//     }
//   }

//   async pause(timestamp: number): Promise<void> {
//     if (!this.currentChannelId || !this.canSendEvent()) {
//       console.log('[RoomService] Cannot send pause event - not connected');
//       return;
//     }
    
//     console.log('[RoomService] Sending PAUSE event at timestamp:', timestamp);
//     this.isLocalAction = true;
    
//     const event: VideoControlEvent = {
//       channelId: this.currentChannelId,
//       action: 'PAUSE',
//       timestamp: timestamp,
//       userId: this.getCurrentUserId()
//     };

//     this.updateLocalState({
//       ...this.roomState$.value!,
//       isPlaying: false,
//       currentTimestamp: timestamp,
//       lastUpdatedAt: new Date().toISOString()
//     });

//     try {
//       await this.apiService.sendControlEvent(event).toPromise();
//       console.log('[RoomService] PAUSE event sent successfully');
//     } catch (error) {
//       console.error('[RoomService] Failed to send pause event:', error);
//       this.isLocalAction = false;
//     }
//   }

//   async seek(timestamp: number): Promise<void> {
//     if (!this.currentChannelId || !this.canSendEvent()) {
//       console.log('[RoomService] Cannot send seek event - not connected');
//       return;
//     }
    
//     console.log('[RoomService] Sending SEEK event to timestamp:', timestamp);
//     this.isLocalAction = true;
    
//     const event: VideoControlEvent = {
//       channelId: this.currentChannelId,
//       action: 'SEEK',
//       timestamp: timestamp,
//       userId: this.getCurrentUserId()
//     };

//     this.updateLocalState({
//       ...this.roomState$.value!,
//       currentTimestamp: timestamp,
//       lastUpdatedAt: new Date().toISOString()
//     });

//     try {
//       await this.apiService.sendControlEvent(event).toPromise();
//       console.log('[RoomService] SEEK event sent successfully');
//     } catch (error) {
//       console.error('[RoomService] Failed to send seek event:', error);
//       this.isLocalAction = false;
//     }
//   }

//   async changeVideo(videoUrl: string): Promise<void> {
//     if (!this.currentChannelId || !this.canSendEvent()) {
//       console.log('[RoomService] Cannot send change video event - not connected');
//       return;
//     }
    
//     console.log('[RoomService] Sending CHANGE_VIDEO event with URL:', videoUrl);
//     this.isLocalAction = true;
    
//     const event: VideoControlEvent = {
//       channelId: this.currentChannelId,
//       action: 'CHANGE_VIDEO',
//       videoUrl: videoUrl,
//       timestamp: 0,
//       userId: this.getCurrentUserId()
//     };

//     this.updateLocalState({
//       ...this.roomState$.value!,
//       videoUrl: videoUrl,
//       currentTimestamp: 0,
//       isPlaying: false,
//       lastUpdatedAt: new Date().toISOString()
//     });

//     try {
//       await this.apiService.sendControlEvent(event).toPromise();
//       console.log('[RoomService] CHANGE_VIDEO event sent successfully');
//     } catch (error) {
//       console.error('[RoomService] Failed to send change video event:', error);
//       this.isLocalAction = false;
//     }
//   }

//   getRoomState(): Observable<RoomState | null> {
//     return this.roomState$.asObservable();
//   }

//   getConnectionStatus(): Observable<boolean> {
//     return this.isConnected$.asObservable();
//   }

//   getSyncStatus(): Observable<boolean> {
//     return this.isSyncing$.asObservable();
//   }

//   private subscribeToRoomUpdates(channelId: string): void {
//     console.log('[RoomService] Subscribing to WebSocket room updates');
//     this.websocketService.subscribeToRoom(channelId);

//     this.websocketService.getRoomControlEvents(channelId).subscribe(event => {
//       console.log('[RoomService] Room control event received:', event);
      
//       if (!this.isLocalAction) {
//         this.handleRemoteEvent(event);
//       } else {
//         console.log('[RoomService] Ignoring own event');
//       }
//       this.isLocalAction = false;
//     });

//     this.websocketService.getRoomSyncEvents(channelId).subscribe(syncEvent => {
//       console.log('[RoomService] Room sync event received:', syncEvent);
//       this.handleSyncCheck(syncEvent);
//     });
//   }

//   private async handleRemoteEvent(event: VideoControlEvent): Promise<void> {
//     const currentState = this.roomState$.value;
//     if (!currentState) {
//       console.log('[RoomService] No current state, ignoring remote event');
//       return;
//     }

//     console.log('[RoomService] Handling remote event:', event.action);
//     const updatedState = { ...currentState };

//     switch (event.action) {
//       case 'PLAY':
//         updatedState.isPlaying = true;
//         updatedState.currentTimestamp = event.timestamp || currentState.currentTimestamp;
//         updatedState.lastUpdatedAt = new Date().toISOString();
//         break;

//       case 'PAUSE':
//         updatedState.isPlaying = false;
//         updatedState.currentTimestamp = event.timestamp || currentState.currentTimestamp;
//         updatedState.lastUpdatedAt = new Date().toISOString();
//         break;

//       case 'SEEK':
//         updatedState.currentTimestamp = event.timestamp!;
//         updatedState.lastUpdatedAt = new Date().toISOString();
//         break;

//       case 'CHANGE_VIDEO':
//         updatedState.videoUrl = event.videoUrl!;
//         updatedState.currentTimestamp = 0;
//         updatedState.isPlaying = false;
//         updatedState.lastUpdatedAt = new Date().toISOString();
//         break;
//     }

//     this.updateLocalState(updatedState);
//     await this.applyStateToVideo(updatedState);
//   }

//   private async handleSyncCheck(syncEvent: SyncCheckEvent): Promise<void> {
//     const currentState = this.roomState$.value;
//     if (!currentState) return;

//     console.log('[RoomService] Handling sync check:', syncEvent);

//     const updatedState = {
//       ...currentState,
//       currentTimestamp: syncEvent.authoritativeTimestamp,
//       isPlaying: syncEvent.isPlaying,
//       lastUpdatedAt: new Date().toISOString()
//     };

//     this.updateLocalState(updatedState);

//     if (this.youtubePlayer) {
//       const currentTime = await this.getCurrentTime();
//       const drift = Math.abs(currentTime - syncEvent.authoritativeTimestamp);
      
//       console.log('[RoomService] Sync check - drift:', drift);
      
//       if (drift > this.HARD_SYNC_THRESHOLD) {
//         console.log('[RoomService] Hard sync needed, drift:', drift);
//         await this.hardSync(syncEvent.authoritativeTimestamp);
//       }
//     }
//   }

//   private async applyStateToVideo(state: RoomState): Promise<void> {
//     if (!this.youtubePlayer) {
//       console.log('[RoomService] No player available for applying state');
//       return;
//     }

//     console.log('[RoomService] Applying state to video:', state);

//     // Handle video change first
//     if (!state.videoUrl || state.videoUrl.trim() === '') {
//       console.log('[RoomService] Empty video URL, stopping video');
//       try {
//         await this.youtubePlayer.stopVideo();
//       } catch (e) {
//         console.log('[RoomService] Error stopping video:', e);
//       }
//       return;
//     }

//     // Calculate actual timestamp based on elapsed time
//     const actualTimestamp = this.calculateCurrentTimestamp(state);
//     console.log('[RoomService] Calculated timestamp:', actualTimestamp);

//     try {
//       // Get current player time
//       const currentTime = await this.getCurrentTime();
//       console.log('[RoomService] Current player time:', currentTime);

//       // Sync timestamp if drift is significant
//       const drift = Math.abs(currentTime - actualTimestamp);
//       console.log('[RoomService] Drift:', drift);
      
//       if (drift > this.SYNC_THRESHOLD) {
//         console.log('[RoomService] Seeking to correct drift');
//         await this.seekTo(actualTimestamp);
//       }

//       // Get current player state
//       const playerState = await this.youtubePlayer.getPlayerState();
//       const isCurrentlyPlaying = playerState === 1;
//       console.log('[RoomService] Player state:', playerState, 'isPlaying:', isCurrentlyPlaying);

//       // Sync play/pause state
//       if (state.isPlaying && !isCurrentlyPlaying) {
//         console.log('[RoomService] Playing video');
//         await this.playVideoElement();
//       } else if (!state.isPlaying && isCurrentlyPlaying) {
//         console.log('[RoomService] Pausing video');
//         await this.pauseVideoElement();
//       }

//       // Sync playback rate if specified
//       if (state.playbackRate) {
//         const currentRate = await this.getPlaybackRate();
//         if (currentRate !== state.playbackRate) {
//           console.log('[RoomService] Setting playback rate:', state.playbackRate);
//           await this.setPlaybackRate(state.playbackRate);
//         }
//       }
//     } catch (error) {
//       console.error('[RoomService] Error applying state to video:', error);
//     }
//   }

//   private calculateCurrentTimestamp(state: RoomState): number {
//     if (!state.isPlaying) {
//       return state.currentTimestamp;
//     }

//     const lastUpdated = new Date(state.lastUpdatedAt).getTime();
//     const now = Date.now();
//     const elapsedSeconds = (now - lastUpdated) / 1000;
//     const playbackRate = state.playbackRate || 1.0;

//     const calculated = state.currentTimestamp + (elapsedSeconds * playbackRate);
//     return calculated;
//   }

//   // private startDriftMonitoring(): void {
//   //   this.stopDriftMonitoring();
//   //   console.log('[RoomService] Starting drift monitoring');

//   //   this.syncCheckTimer = setInterval(() => {
//   //     this.checkAndCorrectDrift();
//   //   }, this.SYNC_CHECK_INTERVAL);
//   // }

//   // private stopDriftMonitoring(): void {
//   //   if (this.syncCheckTimer) {
//   //     console.log('[RoomService] Stopping drift monitoring');
//   //     clearInterval(this.syncCheckTimer);
//   //     this.syncCheckTimer = null;
//   //   }
//   // }

//   // private async checkAndCorrectDrift(): Promise<void> {
//   //   const state = this.roomState$.value;
//   //   if (!state || !this.youtubePlayer) return;

//   //   try {
//   //     const expectedTime = this.calculateCurrentTimestamp(state);
//   //     const actualTime = await this.getCurrentTime();
//   //     const drift = Math.abs(expectedTime - actualTime);

//   //     if (drift < this.SYNC_THRESHOLD) {
//   //       return;
//   //     }

//   //     console.log('[RoomService] Drift detected:', {
//   //       expected: expectedTime,
//   //       actual: actualTime,
//   //       drift: drift
//   //     });

//   //     if (drift >= this.HARD_SYNC_THRESHOLD) {
//   //       await this.hardSync(expectedTime);
//   //     } else {
//   //       await this.softSync(expectedTime, actualTime);
//   //     }
//   //   } catch (error) {
//   //     console.error('[RoomService] Error checking drift:', error);
//   //   }
//   // }

//   // private async softSync(targetTime: number, currentTime: number): Promise<void> {
//   //   if (!this.youtubePlayer) return;

//   //   const drift = targetTime - currentTime;
//   //   const baseRate = this.roomState$.value?.playbackRate || 1.0;

//   //   console.log('[RoomService] Soft sync - adjusting playback rate');
    
//   //   if (drift > 0) {
//   //     await this.setPlaybackRate(baseRate * 1.07);
//   //   } else {
//   //     await this.setPlaybackRate(baseRate * 0.93);
//   //   }

//   //   setTimeout(async () => {
//   //     if (this.youtubePlayer) {
//   //       await this.setPlaybackRate(baseRate);
//   //     }
//   //   }, Math.abs(drift) * 1000 / 0.07);

//   //   console.log('[RoomService] Soft sync applied');
//   // }

//   private async hardSync(targetTime: number): Promise<void> {
//     if (!this.youtubePlayer) return;

//     console.log('[RoomService] Hard sync: jumping to', targetTime);
//     await this.seekTo(targetTime);

//     if (this.roomState$.value) {
//       this.updateLocalState({
//         ...this.roomState$.value,
//         currentTimestamp: targetTime,
//         lastUpdatedAt: new Date().toISOString()
//       });
//     }
//   }

//   private updateLocalState(state: RoomState): void {
//     console.log('[RoomService] Updating local state:', state);
//     this.roomState$.next(state);
//   }

//   private canSendEvent(): boolean {
//     return this.isConnected$.value && this.currentChannelId !== null;
//   }

//   private getCurrentUserId(): string {
//     const user = this.authService.getCurrentUser();
//     return user?.id || 'unknown';
//   }

//   ngOnDestroy(): void {
//     this.disconnectFromRoom();
//   }

//   private async getCurrentTime(): Promise<number> {
//     if (!this.youtubePlayer) return 0;
//     try {
//       return await this.youtubePlayer.getCurrentTime();
//     } catch (e) {
//       console.error('[RoomService] Error getting current time:', e);
//       return 0;
//     }
//   }

//   private async seekTo(timestamp: number): Promise<void> {
//     if (this.youtubePlayer) {
//       try {
//         await this.youtubePlayer.seekTo(timestamp, true);
//         console.log('[RoomService] Seeked to:', timestamp);
//       } catch (e) {
//         console.error('[RoomService] Error seeking:', e);
//       }
//     }
//   }

//   private async playVideoElement(): Promise<void> {
//     if (this.youtubePlayer) {
//       try {
//         await this.youtubePlayer.playVideo();
//         console.log('[RoomService] Video playing');
//       } catch (e) {
//         console.error('[RoomService] Error playing video:', e);
//       }
//     }
//   }

//   private async pauseVideoElement(): Promise<void> {
//     if (this.youtubePlayer) {
//       try {
//         await this.youtubePlayer.pauseVideo();
//         console.log('[RoomService] Video paused');
//       } catch (e) {
//         console.error('[RoomService] Error pausing video:', e);
//       }
//     }
//   }

//   private async setPlaybackRate(rate: number): Promise<void> {
//     if (this.youtubePlayer) {
//       try {
//         await this.youtubePlayer.setPlaybackRate(rate);
//         console.log('[RoomService] Playback rate set to:', rate);
//       } catch (e) {
//         console.error('[RoomService] Error setting playback rate:', e);
//       }
//     }
//   }

//   private async getPlaybackRate(): Promise<number> {
//     try {
//       return await (this.youtubePlayer?.getPlaybackRate() ?? 1);
//     } catch (e) {
//       console.error('[RoomService] Error getting playback rate:', e);
//       return 1;
//     }
//   }
// }