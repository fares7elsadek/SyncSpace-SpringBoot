import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FriendAcceptedEvent {
   private friendAcceptedEvent = new Subject<void>();
   serverCreated$ = this.friendAcceptedEvent.asObservable();
  
    notifyAcceptedEvent() {
      this.friendAcceptedEvent.next();
    }
}
