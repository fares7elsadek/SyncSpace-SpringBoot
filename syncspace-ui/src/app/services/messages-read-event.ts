import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessagesReadEvent {
  private messageReadEvent = new Subject<void>();
  messageRead$ = this.messageReadEvent.asObservable();

  notifyMessageReadEvent() {
    this.messageReadEvent.next();
  }
}
