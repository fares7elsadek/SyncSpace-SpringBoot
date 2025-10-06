import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DeleteServerEvent {
   private deleteServerEnvet = new Subject<void>();
     deleteServer$ = this.deleteServerEnvet.asObservable();
    
  notifyDeleteServer() {
    this.deleteServerEnvet.next();
  }
}
