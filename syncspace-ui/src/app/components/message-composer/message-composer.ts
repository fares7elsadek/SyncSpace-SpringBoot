import { CommonModule } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, signal, computed, effect, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Subject, takeUntil } from 'rxjs';
import { SendMessageRequest } from '../../models/api.model';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { SendMessageEvent } from '../../services/send-message-event';

@Component({
  selector: 'app-message-composer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './message-composer.html',
  styleUrl: './message-composer.css'
})
export class MessageComposer implements OnInit, OnDestroy, AfterViewInit,OnChanges {
  private destroy$ = new Subject<void>();

  @Input() channelId: string = '';
  @Input() userId: string = '';
  @ViewChild('messageTextarea', { static: false }) textarea!: ElementRef<HTMLTextAreaElement>;

  content = "";
  isTextareaFocused = false;
  isSending = signal<boolean>(false);
  chatName = signal('');

  
  canSend = computed(() => {
    return !!this.channelId.trim() && !this.isSending();
  });

  constructor(
    private apiService: ApiService,
    private toastr: ToastrService,
    private sendMessageEvent:SendMessageEvent
  ) {}

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
  
    this.adjustTextareaHeight();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['channelId'] || changes['userId']) {
      this.loadChatInfo();
    }
  }

  loadChatInfo(){
    if(this.userId.trim()){
      this.apiService.getUserProfile(this.userId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((response)=>{
        this.chatName.set("@" + response.data.username)
      })
      return;
    }
    if(this.channelId.trim()){
      this.apiService.getChannel(this.channelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((res)=>{
        this.chatName.set(res.data.name)
      })
    }
  }

 
  adjustTextareaHeight(): void {
    if (this.textarea?.nativeElement) {
      const textarea = this.textarea.nativeElement;
      
      
      textarea.style.height = 'auto';
      
      
      const newHeight = Math.min(
        Math.max(textarea.scrollHeight, 20), 
        154 
      );
      
      
      textarea.style.height = `${newHeight}px`;
      
     
      if (textarea.scrollHeight > 154) {
        textarea.style.overflowY = 'auto';
      } else {
        textarea.style.overflowY = 'hidden';
      }
    }
  }

  
  getWordCount(): number {
    if (!this.content.trim()) return 0;
    return this.content.trim().split(/\s+/).length;
  }

  
  handleKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
      return;
    }

    if (event.key === 'Enter' && event.ctrlKey) {
      event.preventDefault();
      this.sendMessage();
      return;
    }

    if (event.key === 'Enter' && event.shiftKey) {
      setTimeout(() => this.adjustTextareaHeight(), 0);
      return;
    }

    if (['Backspace', 'Delete'].includes(event.key)) {
      setTimeout(() => this.adjustTextareaHeight(), 0);
    }
  }

 
  async sendMessage(): Promise<void> {
    if (!this.canSend() || !this.content.trim()) return;

    
    if (this.content.trim().length > 2000) {
      this.toastr.error('Message is too long. Maximum 2000 characters allowed.');
      return;
    }

    this.isSending.set(true);

    const request: SendMessageRequest = {
      channelId: this.channelId,
      content: this.content.trim(),
      messageType: 'TEXT',
    };

    this.apiService.sendMessage(request)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Message sent:', response.data);
          this.content = "";
          
          
          setTimeout(() => {
            this.adjustTextareaHeight();
            this.textarea?.nativeElement.focus();
          }, 0);
          
          this.sendMessageEvent.notifyMessageReadEvent(response.data);
        },
        error: (err) => {
          console.error('Send failed:', err);
          const errorMessage = err?.error?.error ?? 'Failed to send message';
          this.toastr.error(errorMessage);
          
          setTimeout(() => {
            this.textarea?.nativeElement.focus();
          }, 100);
        },
        complete: () => {
          this.isSending.set(false);
        }
      });
  }

  focusTextarea(): void {
    this.textarea?.nativeElement.focus();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}