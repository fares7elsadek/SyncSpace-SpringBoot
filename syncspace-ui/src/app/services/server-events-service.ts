import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ServerEventsService {
  private serverCreatedSource = new Subject<void>();
  serverCreated$ = this.serverCreatedSource.asObservable();

  notifyServerCreated() {
    this.serverCreatedSource.next();
  }
}
