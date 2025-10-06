import { Component, OnInit, signal, WritableSignal } from '@angular/core';
import { UserDto } from '../../models/api.model';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebsocketService } from '../../services/websocket.service';
import { combineLatest, filter } from 'rxjs';

@Component({
  selector: 'app-user-area-component',
  imports: [CommonModule,FormsModule],
  templateUrl: './user-area-component.html',
  styleUrl: './user-area-component.css'
})
export class UserAreaComponent implements OnInit {
  user:WritableSignal<UserDto| null> = signal(null);

  constructor(private authService:AuthService,private webocketService:WebsocketService){}

  ngOnInit() {
  // keep user updated
    this.authService.currentUser$.subscribe(user => this.user.set(user));

    // only update status when both user and socket are ready
    combineLatest([
      this.authService.currentUser$,
      this.webocketService.connected$
      ])
      .pipe(filter(([user, connected]) => !!user))
      .subscribe(([user, connected]) => {
        this.user.update(u => ({ ...u!, isOnline: connected }));
      });
    }

    userStatus(online: boolean): string{
      return online ? 'online' : 'offline';
    }

} 
