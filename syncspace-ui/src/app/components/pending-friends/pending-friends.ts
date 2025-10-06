import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { last, Subject, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { FriendshipDto } from '../../models/api.model';
import { ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';
import { FriendAcceptedEvent } from '../../services/friend-accepted-event';


@Component({
  selector: 'app-pending-friends',
  imports: [CommonModule,FormsModule],
  templateUrl: './pending-friends.html',
  styleUrl: './pending-friends.css'
})
export class PendingFriends implements OnInit,OnDestroy {

  pendingRequests:WritableSignal<FriendshipDto[]>=  signal([]);
  pendingNumber = signal(0);
  
  private destroy$ = new Subject<void>();
  constructor(private apiServer:ApiService,private toastr:ToastrService,private friendAcceptedEvent:FriendAcceptedEvent){}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngOnInit(): void {
    this.loadPendingData();
  }

  loadPendingData(){
    this.apiServer.getPendingFriendRequests()
    .pipe(takeUntil(this.destroy$))
    .subscribe({
      next:(response) =>{
        this.pendingRequests.set(response.data);
        console.log(response.data)
      },
      error:(err)=>{
        this.toastr.error(err.error.error);
      }
    })
  }
  

  acceptFriend(requestId: string){
    this.apiServer.acceptFriendRequest(requestId)
    .subscribe({
      next:()=>{
        this.toastr.success("Friend request accepted successfully");
        this.loadPendingData();
        this.friendAcceptedEvent.notifyAcceptedEvent();
      },
      error:(err)=>{
        this.toastr.error(err.error.error);
      }
    })
  }

  declineFriend(requestId: string){
    this.apiServer.rejectFriendRequest(requestId)
    .subscribe({
      next:()=>{
        this.toastr.success("Friend request rejected successfully");
        this.loadPendingData();
        this.pendingNumber.set(this.pendingRequests().length);
      },
      error:(err)=>{
        this.toastr.error(err.error.error);
      }
    })
  }

 

  displayName(firstName: string,lastName: string): string{
    return firstName + " " + lastName;
  }
}
