import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { ChannelSidebarComponent } from '../channel-sidebar-component/channel-sidebar-component';
import { TopBarComponent } from '../top-bar-component/top-bar-component';
import { MessageComposer } from '../message-composer/message-composer';
import { MembersListComponent } from '../members-list-component/members-list-component';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterOutlet } from '@angular/router';
import { ChannelDto } from '../../models/api.model';
import { ApiService } from '../../services/api.service';
import { WebsocketService } from '../../services/websocket.service';
import { connect, delay, filter, Subject, switchMap, take, takeUntil } from 'rxjs';


@Component({
  selector: 'app-server-component',
  standalone: true,
  imports: [ChannelSidebarComponent, TopBarComponent, MembersListComponent, CommonModule,RouterOutlet],
  templateUrl: './server-component.html',
  styleUrl: './server-component.css'
})
export class ServerComponent implements OnInit,OnDestroy {

  channels: WritableSignal<ChannelDto[]> = signal([]);
  private destroy$ = new Subject<void>();
  public channelId = "";
  private serverId = "";

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService:ApiService,
    private websocketService:WebsocketService
  ) {}


  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((params)=>{
        let serverId = params.get("serverId");
        if(serverId){          
          this.serverId = serverId;
          this.handleSubscription();
        }
        let channelId = params.get("channelId");
        if(channelId){
          this.channelId = channelId;
        }
    })
  }

  handleSubscription(){
    this.websocketService.connected$.pipe(
      filter(connect => connect),
      take(1),
      switchMap(() => {
        this.websocketService.subscribeToServerPresence(this.serverId);
        return this.apiService.getChannels(this.serverId);
      }),
      takeUntil(this.destroy$)
    ).subscribe((response) => {
      if(response.data.length > 0){
        const generalChannel = response.data[0];
        this.router.navigate([`/app/server/${this.serverId}/channel/${generalChannel.id}`]);
      }
    });
  }

  ngOnDestroy(): void {
    if(this.serverId.trim())
      this.websocketService.unsubscribeFromServerPresence(this.serverId);
    this.destroy$.next();
    this.destroy$.complete();
  }

 
}
