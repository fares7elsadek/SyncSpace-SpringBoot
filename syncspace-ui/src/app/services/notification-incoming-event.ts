import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationIncomingEvent {
   private notificationEvent = new Subject<void>();
    notification$ = this.notificationEvent.asObservable();
      
    notifyNotification() {
      this.notificationEvent.next();
    }
}
