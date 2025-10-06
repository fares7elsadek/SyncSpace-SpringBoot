import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NewMemberEvent {
    private newMemberEvent = new Subject<void>();
    newMember$ = this.newMemberEvent.asObservable();
  
    notifyNewMemberEvent() {
      this.newMemberEvent.next();
    }
}
