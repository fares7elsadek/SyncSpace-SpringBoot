import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Subject } from 'rxjs';
import { AuthService } from './services/auth.service';
import { WebsocketService } from './services/websocket.service';
import { InviteServerComponent } from './components/invite-server-component/invite-server-component';


@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy{
  private destroy$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private webSocketService: WebsocketService
  ) {}

  async ngOnInit() {
    const isAuthenticated = await this.authService.initAuth();
    
    if (isAuthenticated) {
      await this.webSocketService.connect();
    }
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
    this.webSocketService.disconnect();
  }
}
