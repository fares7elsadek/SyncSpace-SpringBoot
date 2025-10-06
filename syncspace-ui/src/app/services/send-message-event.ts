import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { MessageDto } from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class SendMessageEvent {
  private messageSentEvent = new Subject<MessageDto>();
    messageSent$ = this.messageSentEvent.asObservable();
  
    notifyMessageReadEvent(message: MessageDto) {
      this.messageSentEvent.next(message);
    }
}
