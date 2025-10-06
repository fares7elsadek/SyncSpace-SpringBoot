import { Component, ElementRef, OnDestroy, OnInit, signal, ViewChild, WritableSignal } from '@angular/core';
import { Router, RouterLink, NavigationEnd } from '@angular/router';
import { ServerDto} from '../../models/api.model';
import { ApiService } from '../../services/api.service';
import { filter, Subject, takeUntil } from 'rxjs';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ModalService } from '../../services/modal-service';
import { ServerEventsService } from '../../services/server-events-service';
import { DeleteServerEvent } from '../../services/delete-server-event';

@Component({
  selector: 'app-server-list-component',
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './server-list-component.html',
  styleUrl: './server-list-component.css'
})
export class ServerListComponent implements OnInit, OnDestroy {
  
  servers: WritableSignal<ServerDto[]> = signal([]);
  @ViewChild('serverTooltip', { static: false }) serverTooltip!: ElementRef<HTMLDivElement>;
  @ViewChild('tooltipPortal', { static: false }) tooltipPortal!: ElementRef<HTMLDivElement>;
  showTooltip = false;
  tooltipText = '';
  activeServerId = signal<string | null>(null);

  
  private destroy$ = new Subject<void>();
  private tooltipTimeout?: number;

  constructor(
    private apiService: ApiService,
    private router: Router,
    public modal: ModalService,
    private serverEvent: ServerEventsService,
    private deletServerEvent:DeleteServerEvent
  ) {
    // Initialize active server ID on construction
    this.updateActiveServerId();
    
    // Subscribe to router events to update active server
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.updateActiveServerId();
    });
  }

  ngOnInit(): void {
    this.loadServers();
    this.serverEvent.serverCreated$.subscribe(() => {
      this.loadServers();
    });
    this.deletServerEvent.deleteServer$
    .pipe(takeUntil(this.destroy$))
    .subscribe(()=>{
      this.loadServers();
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.tooltipTimeout) {
      clearTimeout(this.tooltipTimeout);
    }
  }

  private updateActiveServerId(): void {
    // Extract server ID from URL: /app/server/{serverId}/...
    const match = this.router.url.match(/\/app\/server\/([^\/]+)/);
    this.activeServerId.set(match ? match[1] : null);
  }

  loadServers(): void {
    this.apiService.getServers()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.servers.set(response.data);
        },
        error: (error) => {
          console.error('Failed to load servers:', error);
        }
      });
  }

  showServerTooltip(event: MouseEvent, serverName: string) {
    if (this.tooltipTimeout) {
      clearTimeout(this.tooltipTimeout);
    }

    const button = event.currentTarget as HTMLButtonElement;
    const buttonRect = button.getBoundingClientRect();
    
    this.tooltipText = serverName;
    this.updateTooltipPosition(buttonRect);
    
    this.tooltipTimeout = setTimeout(() => {
      this.showTooltip = true;
    }, 50);
  }

  hideServerTooltip() {
    if (this.tooltipTimeout) {
      clearTimeout(this.tooltipTimeout);
    }
    
    this.showTooltip = false;
  }

  private updateTooltipPosition(buttonRect: DOMRect) {
    if (this.serverTooltip && this.serverTooltip.nativeElement) {
      const tooltipEl = this.serverTooltip.nativeElement;
      const buttonCenterY = buttonRect.top + (buttonRect.height / 2);
      
      tooltipEl.style.left = `${buttonRect.right + 12}px`;
      tooltipEl.style.top = `${buttonCenterY}px`;
      
      setTimeout(() => {
        const tooltipRect = tooltipEl.getBoundingClientRect();
        const viewportHeight = window.innerHeight;
        
        if (buttonCenterY + (tooltipRect.height / 2) > viewportHeight - 10) {
          tooltipEl.style.top = `${viewportHeight - tooltipRect.height - 10}px`;
        } else if (buttonCenterY - (tooltipRect.height / 2) < 10) {
          tooltipEl.style.top = `10px`;
        }
      }, 0);
    }
  }

  trackByServerId(index: number, server: ServerDto): string | number {
    return server.id;
  }
}