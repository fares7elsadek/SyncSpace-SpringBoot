import { AfterViewInit, ChangeDetectorRef, Component, ElementRef, OnChanges, OnDestroy, OnInit, signal, SimpleChanges, ViewChild, WritableSignal } from '@angular/core';
import { catchError, combineLatest, debounceTime, distinctUntilChanged, EMPTY, filter, finalize, interval, merge, of, retry, Subject, Subscription, switchMap, take, takeUntil, tap, timer } from 'rxjs';
import { MessageDto, PaginatedMessage } from '../../models/api.model';
import { ApiService } from '../../services/api.service';
import { WebsocketService } from '../../services/websocket.service';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { MessagesReadEvent } from '../../services/messages-read-event';
import { SendMessageEvent } from '../../services/send-message-event';
import { MessageComposer } from '../message-composer/message-composer';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


interface ChatState {
  channelId: string;
  cursor: string;
  isInitialLoad: boolean;
  lastProcessedMessageId: string;
}

@Component({
  selector: 'app-channel-chat-messages-component',
  imports: [MessageComposer,CommonModule,FormsModule],
  templateUrl: './channel-chat-messages-component.html',
  styleUrl: './channel-chat-messages-component.css'
})
export class ChannelChatMessagesComponent implements OnInit,OnDestroy,AfterViewInit {
  private readonly destroy$ = new Subject<void>();
    private readonly scrollSubject = new Subject<Event>();
    private readonly messageQueue = new Subject<MessageDto>();
    
    private heartbeatSub?: Subscription;
    private websocketSub?: Subscription;
    private routeSub?: Subscription;
    private messageProcessingSub?: Subscription;
    
    private readonly HEARTBEAT_INTERVAL = 4 * 60 * 1000; // 4 minutes
    private readonly SCROLL_THRESHOLD = 50;
    private readonly SCROLL_DEBOUNCE = 100;
    private readonly MESSAGE_BATCH_SIZE = 20;
    private readonly RETRY_ATTEMPTS = 3;
    private readonly RETRY_DELAY = 1000;
    
    public chatState: ChatState = {
      channelId: '',
      cursor: '',
      isInitialLoad: true,
      lastProcessedMessageId: ''
    };
    
    public messages = signal<PaginatedMessage>({
      messages: [],
      nextCursor: "",
      hasMore: true
    });
  

    public isLoadingMessages: WritableSignal<boolean> = signal(false);
    public isLoadingUser: WritableSignal<boolean> = signal(false);
    public connectionStatus: WritableSignal<boolean> = signal(true);
    
    @ViewChild('scrollContainer') scrollContainer!: ElementRef;
  
    constructor(
      private apiService: ApiService,
      private websocketService: WebsocketService,
      private activatedRoute: ActivatedRoute,
      private toastr: ToastrService,
      private messageRead: MessagesReadEvent,
      private cdr: ChangeDetectorRef
    ) {}
  
    ngOnInit(): void {
      this.setupScrollHandler();
      this.setupMessageQueue();
      this.setupRouteHandler();
      this.setupConnectionMonitoring();
    }
  
    ngAfterViewInit(): void {
      this.setupScrollListener();
    }
  
    ngOnDestroy(): void {
      this.cleanup();
    }
  
    private setupScrollHandler(): void {
      this.scrollSubject.pipe(
        debounceTime(this.SCROLL_DEBOUNCE),
        takeUntil(this.destroy$)
      ).subscribe(() => this.checkScrollPosition());
    }
  
    private setupMessageQueue(): void {
      this.messageProcessingSub = this.messageQueue.pipe(
        debounceTime(50), 
        takeUntil(this.destroy$)
      ).subscribe(message => this.processMessage(message));
    }
  
    private setupConnectionMonitoring(): void {
      this.websocketService.connected$.pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged(),
        tap(connected => console.log('WebSocket connection status:', connected))
      ).subscribe(connected => {
        this.connectionStatus.set(connected);
        
        if (connected && this.chatState.channelId) {
          setTimeout(() => {
            this.reconnectToChannel();
          }, 100);
        } else if (!connected) {
          this.websocketSub?.unsubscribe();
        }
      });
    }
  
    private setupRouteHandler(): void {
      this.routeSub = combineLatest([
        this.activatedRoute.paramMap,
        this.activatedRoute.queryParamMap
      ]).pipe(
        takeUntil(this.destroy$),
        distinctUntilChanged(([prevParams, prevQuery], [currParams, currQuery]) => 
          prevParams.get('channelId') === currParams.get('channelId') &&
          prevQuery.get('user') === currQuery.get('user')
        ),
        switchMap(([params, queryParams]) => this.handleRouteChange(params))
      ).subscribe();
    }
  
    private handleRouteChange(params: any) {
      const newChannelId = params.get("channelId") ?? "";
  
      // Clean up previous channel
      this.cleanupCurrentChannel();
  
      // Update state
      this.updateChatState(newChannelId);
  
      // Reset UI state
      this.resetUIState();
  
      // Load new data
      return this.loadChatData();
    }
  
    private updateChatState(channelId: string): void {
      this.chatState = {
        channelId,
        cursor: '',
        isInitialLoad: true,
        lastProcessedMessageId: ''
      };
    }
  
    private resetUIState(): void {
      this.messages.set({
        messages: [],
        nextCursor: "",
        hasMore: true
      });
      this.isLoadingMessages.set(!!this.chatState.channelId);
    }
  
    private loadChatData() {
      const loadTasks = [];
  
      if (this.chatState.channelId) {
        this.setupChannelSubscription();
        this.startHeartbeat();
        loadTasks.push(this.loadMessages());
      }
  
      return merge(...loadTasks);
    }
  
    
  private setupChannelSubscription(): void {
    
    this.websocketSub?.unsubscribe();
    
    
    this.websocketService.connected$.pipe(
      filter(connected => connected),
      take(1),
      switchMap(() => {
        this.websocketService.startViewing(this.chatState.channelId);
        this.websocketService.subscribeToChannel(this.chatState.channelId);
        console.log("I am here waiting for you - Connected and subscribed");
        
        return this.websocketService.getChannelMessages(this.chatState.channelId);
      }),
      takeUntil(this.destroy$),
      retry({
        count: 3,
        delay: 1000
      })
    ).subscribe({
      next: (message) => {
        console.log("New message received:", message);
        this.messageQueue.next(message);
      },
      error: (error) => {
        console.error('Channel messages error:', error);
        this.connectionStatus.set(false);
        setTimeout(() => this.setupChannelSubscription(), 2000);
      }
    });
  
    
    
  
    this.websocketSub = new Subscription();
  }
  
  
 
  
  
  
  
    private loadMessages() {
      if (!this.chatState.channelId.trim()) return of(null);
      
      this.isLoadingMessages.set(true);
      const requestChannelId = this.chatState.channelId;
  
      return this.apiService.getChannelMessages(
        this.chatState.channelId, 
        this.MESSAGE_BATCH_SIZE, 
        this.chatState.cursor
      ).pipe(
        retry({
          count: this.RETRY_ATTEMPTS,
          delay: (error, retryCount) => timer(this.RETRY_DELAY * retryCount)
        }),
        catchError(error => {
          console.error('Failed to load messages:', error);
          this.toastr.error(error.error?.error || 'Failed to load messages');
          return of(null);
        }),
        finalize(() => this.isLoadingMessages.set(false)),
        filter(response => !!response && requestChannelId === this.chatState.channelId),
        tap(response => this.handleMessagesResponse(response!))
      );
    }
  
    private handleMessagesResponse(response: any): void {
      this.messages.update(state => {
        const combined = this.chatState.isInitialLoad
          ? response.data.messages 
          : [...state.messages, ...response.data.messages];
  
        return {
          messages: this.deduplicateMessages(combined),
          hasMore: response.data.hasMore,
          nextCursor: response.data.nextCursor ?? null
        };
      });
  
      this.chatState.cursor = response.data.nextCursor ?? "";
      this.messageRead.notifyMessageReadEvent();
  
      if (this.chatState.isInitialLoad) {
        setTimeout(() => {
          this.scrollToBottom();
          this.chatState.isInitialLoad = false;
        }, 100);
      }
    }
  
    
  
    private loadOlderMessages(): void {
      if (!this.chatState.channelId.trim() || 
          !this.messages().hasMore || 
          this.isLoadingMessages() ||
          !this.chatState.cursor) { 
        return;
      }
      
      this.isLoadingMessages.set(true);
      const requestChannelId = this.chatState.channelId;
      const container = this.scrollContainer.nativeElement;
      const prevScrollHeight = container.scrollHeight;
      const prevScrollTop = container.scrollTop;
  
      this.apiService.getChannelMessages(
        this.chatState.channelId, 
        this.MESSAGE_BATCH_SIZE, 
        this.chatState.cursor
      ).pipe(
        takeUntil(this.destroy$),
        retry({
          count: this.RETRY_ATTEMPTS,
          delay: (error, retryCount) => timer(this.RETRY_DELAY * retryCount)
        }),
        catchError(error => {
          console.error('Failed to load older messages:', error);
          this.toastr.error(error.error?.error || 'Failed to load messages');
          return of(null);
        }),
        finalize(() => this.isLoadingMessages.set(false)),
        filter(response => !!response && requestChannelId === this.chatState.channelId)
      ).subscribe(response => {
        this.messages.update(state => {
          const combined = [...response!.data.messages, ...state.messages];
          return {
            hasMore: response!.data.hasMore,
            nextCursor: response!.data.nextCursor ?? null,
            messages: this.deduplicateMessages(combined)
          };
        });
        
        this.chatState.cursor = response!.data.nextCursor ?? "";
  
        // Maintain scroll position
        requestAnimationFrame(() => {
          const newScrollHeight = container.scrollHeight;
          const heightDifference = newScrollHeight - prevScrollHeight;
          container.scrollTop = prevScrollTop + heightDifference;
          this.cdr.detectChanges();
        });
      });
    }
  
    private processMessage(message: MessageDto): void {
      // Check if message is already processed (duplicate prevention)
      const messageId = message.messageId || message.messageId;
      if (!messageId) {
        console.warn('Received message without ID, skipping:', message);
        return;
      }
  
      // Check if we already have this message
      const existingMessages = this.messages().messages;
      const isDuplicate = existingMessages.some(msg => 
        (msg.messageId || msg.messageId) === messageId
      );
  
      if (isDuplicate) {
        return;
      }
  
      this.messages.update(state => {
        const newMessages = this.deduplicateMessages([...state.messages, message]);
        return {
          ...state,
          messages: newMessages
        };
      });
  
      this.cdr.detectChanges();
  
      // Auto-scroll if user is near bottom
      requestAnimationFrame(() => {
        const container = this.scrollContainer?.nativeElement;
        if (container) {
          const isNearBottom = container.scrollHeight - container.scrollTop - container.clientHeight < 100;
          if (isNearBottom) {
            this.scrollToBottom();
          }
        }
      });
    }
  
    private deduplicateMessages(messages: any[]): any[] {
      const seen = new Map();
      return messages.filter(msg => {
        const id = msg.id || msg.messageId;
        const createdAt = msg.createdAt || msg.created_at || msg.timestamp;
        
        if (!id) return false;
        
        const existing = seen.get(id);
        if (!existing) {
          seen.set(id, { createdAt });
          return true;
        }
        
        // If we have timestamp/createdAt info, keep the newer one
        if (createdAt && existing.createdAt) {
          const currentTime = new Date(createdAt).getTime();
          const existingTime = new Date(existing.createdAt).getTime();
          
          if (currentTime > existingTime) {
            seen.set(id, { createdAt });
            return true;
          }
          return false;
        }
        
        // No timestamp info, keep the first occurrence
        return false;
      });
    }
  
    private startHeartbeat(): void {
      this.stopHeartbeat();
      this.heartbeatSub = interval(this.HEARTBEAT_INTERVAL).pipe(
        takeUntil(this.destroy$)
      ).subscribe(() => {
        if (this.chatState.channelId) {
          this.websocketService.startViewing(this.chatState.channelId);
        }
      });
    }
  
    private stopHeartbeat(): void {
      this.heartbeatSub?.unsubscribe();
      this.heartbeatSub = undefined;
    }
  
    private reconnectToChannel(): void {
      if (this.chatState.channelId) {
        console.log('Reconnecting to channel:', this.chatState.channelId);
        // Clean up existing subscription first
        this.websocketSub?.unsubscribe();
        
        // Re-establish the full channel subscription
        this.setupChannelSubscription();
      }
    }
  
    private cleanupCurrentChannel(): void {
      if (this.chatState.channelId) {
        this.websocketService.stopViewing(this.chatState.channelId);
        this.websocketService.unsubscribeFromChannel(this.chatState.channelId);
      }
      this.stopHeartbeat();
      this.websocketSub?.unsubscribe();
    }
  
    private setupScrollListener(): void {
      if (this.scrollContainer?.nativeElement) {
        this.scrollContainer.nativeElement.addEventListener('scroll', this.onScroll.bind(this));
      }
    }
  
    private onScroll(event: Event): void {
      this.scrollSubject.next(event);
    }
  
    private checkScrollPosition(): void {
      const container = this.scrollContainer?.nativeElement;
      if (!container) return;
  
      const isNearTop = container.scrollTop <= this.SCROLL_THRESHOLD;
      
      if (isNearTop && 
          this.messages()?.hasMore && 
          !this.isLoadingMessages() && 
          this.chatState.cursor) {
        this.loadOlderMessages();
      }
    }
  
    private scrollToBottom(): void {
      if (!this.scrollContainer?.nativeElement) return;
      const container = this.scrollContainer.nativeElement;
      container.scrollTop = container.scrollHeight;
    }
  
    private cleanup(): void {
      this.cleanupCurrentChannel();
      
      if (this.scrollContainer?.nativeElement) {
        this.scrollContainer.nativeElement.removeEventListener('scroll', this.onScroll.bind(this));
      }
      
      this.routeSub?.unsubscribe();
      this.messageProcessingSub?.unsubscribe();
      
      this.destroy$.next();
      this.destroy$.complete();
    }
  
    // Public methods for template
    trackByMessageId(index: number, message: any): any {
      return message.id || message.messageId || index;
    }
  
    chatStatus(online: boolean): string {
      return online ? 'online' : 'offline';
    }
  
    isCodeBlock(text: string): boolean {
      return text.startsWith('```') && text.endsWith('```');
    }
  
    onAvatarLoad(): void {}
    onAvatarError(): void {}
    onMessageAvatarError(event: any): void {
      event.target.style.display = 'none';
    }
}
