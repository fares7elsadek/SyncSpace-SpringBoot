import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RoomConnectionEvent {
  private roomConnectionEvent = new Subject<void>();
  roomConnection$ = this.roomConnectionEvent.asObservable();
        
  notifyRoomConnection() {
    this.roomConnectionEvent.next();
  }
}
