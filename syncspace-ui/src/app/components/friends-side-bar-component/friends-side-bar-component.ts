import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { UserAreaComponent } from '../user-area-component/user-area-component';
import { Subject, takeUntil } from 'rxjs';
import { UserChat } from '../../models/api.model';
import { ApiService } from '../../services/api.service';
import { Router, RouterLink } from '@angular/router';
import { WebsocketService } from '../../services/websocket.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FriendAcceptedEvent } from '../../services/friend-accepted-event';
import { MessagesReadEvent } from '../../services/messages-read-event';

@Component({
  selector: 'app-friends-side-bar-component',
  imports: [UserAreaComponent,CommonModule,FormsModule,RouterLink],
  templateUrl: './friends-side-bar-component.html',
  styleUrl: './friends-side-bar-component.css'
})
export class FriendsSideBarComponent implements OnInit,OnDestroy{
  chats: WritableSignal<UserChat[]> = signal([]); 
  private destroy$ = new Subject<void>();


  constructor(
    private apiService: ApiService,
    private router:Router,
    private websocketService:WebsocketService,
    private tostr:ToastrService,
    private friendAcceptedEvent:FriendAcceptedEvent,
    private messageRead:MessagesReadEvent
  ){}

  ngOnInit(): void {
    this.loadChats();
    this.websocketService.userPresence$
    .pipe(takeUntil(this.destroy$))
    .subscribe((message) => {
      this.updateFriendOnlineStatus(message.userId, message.status);
    });
    this.friendAcceptedEvent.serverCreated$
    .pipe(takeUntil(this.destroy$))
    .subscribe(()=>{
      this.loadChats();
    })
    this.messageRead.messageRead$
    .pipe(takeUntil(this.destroy$))
    .subscribe(()=>{
      this.loadChats();
    })

    this.websocketService.generalMessages$
        .pipe(takeUntil(this.destroy$))
        .subscribe(()=>{
          this.loadChats();
        })

    
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  updateFriendOnlineStatus(friendId: string, status: string) {
    this.chats.update(chatsList => 
      chatsList.map(chat => 
        chat.user.id === friendId 
          ? { 
              ...chat, 
              user: { ...chat.user, isOnline: this.getStatus(status) } 
            } 
          : chat
      )
    );
  }

  
  getStatus(status:string): boolean{
    return status === 'ONLINE' ? true : false;
  }

  loadChats(){
    this.apiService.getUserChats()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next:(response)=>{
        this.chats.set(response.data);
      },
      error:(err)=>{
        this.tostr.error(err.error.error);
      }
    })
  }

  isActive(route: string): boolean{
    return this.router.url.startsWith(route);
  }

  chatStatus(online: boolean): string {
    return online ? 'online' : 'offline';
  }

}
