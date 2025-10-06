import { Component, OnDestroy, OnInit, signal, WritableSignal, computed } from '@angular/core';
import { catchError, combineLatest, delay, filter, of, Subject, switchMap, take, takeUntil, timeout, timer } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ActivatedRoute } from '@angular/router';
import { ServerMember } from '../../models/api.model';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WebsocketService } from '../../services/websocket.service';
import { NewMemberEvent } from '../../services/new-member-event';

interface GroupedMembers {
  owners: ServerMember[];
  admins: ServerMember[];
  members: ServerMember[];
}

@Component({
  selector: 'app-members-list-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './members-list-component.html',
  styleUrl: './members-list-component.css'
})
export class MembersListComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  serverId = signal('');
  public members: WritableSignal<ServerMember[]> = signal([]);
  public searchTerm = signal('');

  public filteredMembers = computed(() => {
    const term = this.searchTerm().toLowerCase();
    return this.members().filter(member => 
      member.serverUserDto.username.toLowerCase().includes(term)
    );
  });

  public groupedMembers = computed((): GroupedMembers => {
    const filtered = this.filteredMembers();
    return {
      owners: filtered.filter(member => member.role === 'OWNER'),
      admins: filtered.filter(member => member.role === 'ADMIN'),
      members: filtered.filter(member => member.role === 'USER')
    };
  });

  public onlineStats = computed(() => {
    const grouped = this.groupedMembers();
    return {
      owners: {
        total: grouped.owners.length,
        online: grouped.owners.filter(m => m.serverUserDto.isOnline).length
      },
      admins: {
        total: grouped.admins.length,
        online: grouped.admins.filter(m => m.serverUserDto.isOnline).length
      },
      members: {
        total: grouped.members.length,
        online: grouped.members.filter(m => m.serverUserDto.isOnline).length
      }
    };
  });

  constructor(
    private apiService: ApiService,
    private activatedRoute: ActivatedRoute,
    private tostr: ToastrService,
    private websocketService: WebsocketService,
    private newMemberEvent:NewMemberEvent
  ) {}

  handleSubscription() {
    this.websocketService.connected$.pipe(
      filter(connect => connect),
      take(1),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.loadMembers();
      this.websocketService.getServerPresence(this.serverId()).pipe(
        takeUntil(this.destroy$)
      ).subscribe(() => {
        this.loadMembers();
      });
    });
  }

  ngOnInit(): void {
    this.activatedRoute.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((param) => {
        let serverId = param.get("serverId");
        if (serverId) {
          const previousServerId = this.serverId();
          this.serverId.set(serverId);
          if (previousServerId && previousServerId !== serverId) {
            this.members.set([]);
          }
          this.handleSubscription();
        }
      });

    this.newMemberEvent.newMember$.pipe(takeUntil(this.destroy$))
    .subscribe(()=>{
      this.loadMembers();
    })
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadMembers() {
    if (!this.serverId().trim()) return;
    
    this.apiService.getServerMembers(this.serverId())
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.members.set(response.data);
        },
        error: (err) => {
          this.tostr.error(err.error?.error || 'Failed to load members');
        }
      });
  }

  memberStatus(online: boolean): string {
    return online ? 'online' : 'offline';
  }

  getRoleIcon(role: string): string {
    switch (role) {
      case 'OWNER': return '👑';
      case 'ADMIN': return '🛡️';
      default: return '';
    }
  }

  getRoleDisplayName(role: string): string {
    switch (role) {
      case 'OWNER': return 'Owner';
      case 'ADMIN': return 'Admin';
      case 'USER': return 'Members';
      default: return role;
    }
  }

  onSearchChange(event: Event) {
    const target = event.target as HTMLInputElement;
    this.searchTerm.set(target.value);
  }
}