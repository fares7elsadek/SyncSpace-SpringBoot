import { Component, OnDestroy, OnInit } from '@angular/core';
import { ServerListComponent } from '../server-list-component/server-list-component';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { AppGlobalModal } from '../app-global-modal/app-global-modal';
import { WebsocketService } from '../../services/websocket.service';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { NotificationIncomingEvent } from '../../services/notification-incoming-event';
import { FriendAcceptedEvent } from '../../services/friend-accepted-event';


@Component({
  selector: 'app-main-component',
  imports: [ServerListComponent,RouterOutlet,AppGlobalModal],
  templateUrl: './main-component.html',
  styleUrl: './main-component.css'
})
export class MainComponent implements OnInit,OnDestroy{

  private destroy$ = new Subject<void>();
  constructor(private websocket:WebsocketService
    ,private toastr:ToastrService,private notificationEvent:NotificationIncomingEvent,
  private friendAcceptedEvent:FriendAcceptedEvent){}

  ngOnInit(): void {
    this.websocket.notifications$
    .pipe(takeUntil(this.destroy$))
    .subscribe((message)=>{
      if(message.type === 'FRIEND_REQUEST' || message.type === 'FRIEND_ACCEPTED'){
          this.toastr.success(message.content);
          this.notificationEvent.notifyNotification();
          this.friendAcceptedEvent.notifyAcceptedEvent();
      }
            
    })
  }
 
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
