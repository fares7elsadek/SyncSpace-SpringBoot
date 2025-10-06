import { Injectable, signal } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InviteModal {

  private modalEvent = new Subject<{serverId:string,code:string}>();
  modal$ = this.modalEvent.asObservable();
  
  notifyModalEvent(serverId:string,code:string) {
    this.modalEvent.next({serverId,code});
  }

}
