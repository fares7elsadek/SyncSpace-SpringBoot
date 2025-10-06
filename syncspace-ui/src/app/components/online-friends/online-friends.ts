import { CommonModule } from '@angular/common';
import { Component, signal, WritableSignal } from '@angular/core';
import { FriendshipDto } from '../../models/api.model';
import { Subject, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { WebsocketService } from '../../services/websocket.service';


@Component({
  selector: 'app-online-friends',
  imports: [CommonModule],
  templateUrl: './online-friends.html',
  styleUrl: './online-friends.css'
})
export class OnlineFriends {
  friends: WritableSignal<FriendshipDto[]> = signal([]);
  onlineNumber= signal(0);
  
  private destroy$ = new Subject<void>();
  activeFriendOptions = signal("");

  constructor(private apiServer:ApiService,private toastr:ToastrService,private websocketService:WebsocketService){}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit(): void {
    this.loadFriends();
    this.websocketService.userPresence$
    .pipe(takeUntil(this.destroy$))
    .subscribe((message) => {
      this.updateFriendOnlineStatus(message.userId, message.status);
      this.onlineNumber.set(this.onlineFriends());
    });
  }


  loadFriends(){
    this.apiServer.getFriends()
    .subscribe({
      next:(response)=>{
        this.friends.set(response.data);
      },
      error:(err)=>{
        this.toastr.error(err.error.error);
      }
    })
  }

  updateFriendOnlineStatus(friendId: string, status: string) {
  this.friends.update(friendsList => 
    friendsList.map(friend => 
      friend.user.id === friendId 
        ? { 
            ...friend, 
            user: { ...friend.user, isOnline: this.getStatus(status) } 
          } 
        : friend
    )
  );
}

  startDM(name: any){

  }
  callFriend(name: any){

  }

  showFriendOptions(name: any){
    
  }

  getStatus(status:string): boolean{
    return status === 'ONLINE' ? true : false;
  }

  friendStatus(online: boolean): string{
    return online ? 'online' : 'offline';
  }

  onlineFriends(){
    return this.friends().filter((f) => f.user.isOnline == true).length;
  }
}
