import { Component, EventEmitter, OnDestroy, OnInit, Output, signal, WritableSignal, computed } from '@angular/core';
import { ChannelDto, CreateChannelRequest, ServerDto, ServerMember, UserDto, RoomViewer } from '../../models/api.model';
import {  Subject, takeUntil, interval } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserAreaComponent } from '../user-area-component/user-area-component';
import { NewMemberEvent } from '../../services/new-member-event';
import { DeleteServerEvent } from '../../services/delete-server-event';
import { RoomChannelComponent } from '../room-channel-component/room-channel-component';
import { RoomConnectionEvent } from '../../services/room-connection-event';

interface ChannelWithViewers extends ChannelDto {
  viewers?: RoomViewer[];
}

@Component({
  selector: 'app-channel-sidebar-component',
  imports: [CommonModule, FormsModule, RouterLink, UserAreaComponent],
  templateUrl: './channel-sidebar-component.html',
  styleUrl: './channel-sidebar-component.css'
})
export class ChannelSidebarComponent implements OnInit, OnDestroy {
  channels: WritableSignal<ChannelWithViewers[]> = signal([]);
  server: WritableSignal<ServerDto | null> = signal(null);
  showCreateChannelModal = signal(false);
  isCreatingChannel = signal(false);
  showServerMenu = signal(false);
  errorMessage = signal("");
  showAddMemberModal = signal(false);
  memberToAdd = '';
  showInviteModal = signal(false);
  inviteCode = "";
  inviteCodeUrl = signal('');
  showDeleteModal = signal(false);
  deleteConfirmation = '';
  isAddingMember = signal(false);
  serverMember: WritableSignal<ServerMember | null> = signal(null);
  channelTypeToCreate: 'TEXT' | 'STREAMING' = 'TEXT';

  // Computed signals to filter channels by type
  textChannels = computed(() => this.channels().filter(c => c.type === 'TEXT'));
  streamingChannels = computed(() => this.channels().filter(c => c.type === 'STREAMING'));

  private destroy$ = new Subject<void>();
  serverId = "";

  newChannel: CreateChannelRequest = {
    name: "",
    description: "",
    isPrivate: false,
    serverId: this.serverId,
    type: "TEXT"
  }

  constructor(
    private apiService: ApiService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private tostr: ToastrService,
    private newMemberEvent: NewMemberEvent,
    private deleteSeverEvent: DeleteServerEvent,
    private roomConnectionEvent:RoomConnectionEvent
  ) {}

  ngOnInit() {
    this.activatedRoute.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((params) => {
        const id = params.get('serverId');
        if (!id) {
          console.warn('No serverId found in route');
          return;
        }

        this.serverId = id;
        this.newChannel.serverId = id;

        this.loadServerInfo();
        this.loadChannels();
        this.loadServerMemberInfo();
        this.startViewersPolling();
        this.roomConnectionEvent
        .roomConnection$.pipe(takeUntil(this.destroy$))
        .subscribe(() => this.loadViewersForStreamingChannels())
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  startViewersPolling() {
    interval(5000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.loadViewersForStreamingChannels();
      });
  }


  loadServerMemberInfo() {
    if (!this.serverId.trim()) return;
    this.apiService.getServerMember(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe((response) => {
        this.serverMember.set(response.data);
      });
  }

  isAdmin(): boolean {
    return this.serverMember()?.role === 'OWNER' || this.serverMember()?.role === 'ADMIN';
  }

  loadChannels() {
    if (!this.serverId) return;
    this.apiService.getChannels(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.channels.set(response.data);
          // Load viewers for streaming channels
          this.loadViewersForStreamingChannels();
        },
        error: (err) => {
          this.tostr.error(err.error.error);
        }
      });
  }

  loadViewersForStreamingChannels() {
    const streamingChans = this.streamingChannels();
    streamingChans.forEach(channel => {
      this.loadChannelViewers(channel.id);
    });
  }

  loadChannelViewers(channelId: string) {
    this.apiService.getRoomViewers(channelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          const updatedChannels = this.channels().map(ch =>
            ch.id === channelId ? { ...ch, viewers: response.data } : ch
          );
          this.channels.set(updatedChannels);
        },
        error: (err) => {
          console.error('Failed to load viewers for channel', channelId, err);
        }
      });
  }



  loadServerInfo() {
    if (!this.serverId) return;
    this.apiService.getServer(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.server.set(response.data);
        },
        error: (err) => {
          this.tostr.error(err.error.error);
        }
      });
  }

  openCreateChannelModal(type: 'TEXT' | 'STREAMING') {
    this.channelTypeToCreate = type;
    this.newChannel.type = type;
    this.showCreateChannelModal.set(true);
  }

  createChannel() {
    if (!this.newChannel.name.trim()) return;
    this.isCreatingChannel.set(true);
    this.apiService.createChannel(this.newChannel)
      .subscribe({
        next: () => {
          this.tostr.success("Channel created successfully");
          this.loadChannels();
          this.closeModal();
        },
        error: (err) => {
          this.errorMessage.set(err.error.error);
          this.isCreatingChannel.set(false);
        }
      });
  }

  isActive(route: string): boolean {
    return this.router.url == route;
  }

  closeModal() {
    this.showCreateChannelModal.set(false);
    this.isCreatingChannel.set(false);
    this.newChannel = {
      name: "",
      description: "",
      isPrivate: false,
      serverId: this.serverId,
      type: this.channelTypeToCreate
    };
    this.errorMessage.set("");
  }

  toggleServerMenu() {
    this.showServerMenu.set(!this.showServerMenu());
  }

  closeServerMenu() {
    this.showServerMenu.set(false);
  }

  openAddMemberModal() {
    this.closeServerMenu();
    this.showAddMemberModal.set(true);
  }

  closeAddMemberModal() {
    this.showAddMemberModal.set(false);
    this.memberToAdd = '';
    this.isAddingMember.set(false);
    this.errorMessage.set('');
  }

  addMember() {
    if (!this.serverId.trim() || !this.memberToAdd.trim()) {
      this.errorMessage.set("Username can't be blank");
      return;
    }
    this.isAddingMember.set(true);
    this.apiService.addServerMember(this.serverId, this.memberToAdd)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.tostr.success(response.message);
          this.isAddingMember.set(false);
          this.closeAddMemberModal();
          this.newMemberEvent.notifyNewMemberEvent();
        },
        error: (err) => {
          this.tostr.error(err.error.error);
          this.errorMessage.set(err.error.error);
          this.isAddingMember.set(false);
        }
      });
  }

  openInviteModal() {
    this.closeServerMenu();
    this.showInviteModal.set(true);
  }

  closeInviteModal() {
    this.showInviteModal.set(false);
  }

  generateInviteCode() {
    if (!this.serverId.trim()) {
      this.tostr.error("Server id not visible");
      return;
    }
    this.apiService.getInviteCode(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.inviteCode = response.data.code;
          this.inviteCodeUrl.set(`http://${window.location.hostname}:${window.location.port}/invite/server/${this.serverId}?code=${this.inviteCode}`);
          this.openInviteModal();
        },
        error: (err) => {
          this.tostr.error(err.error.error);
        }
      });
  }

  copyInviteCode() {
    if (!this.inviteCodeUrl()) return;

    navigator.clipboard.writeText(this.inviteCodeUrl())
      .then(() => {
        this.tostr.success("Invite link copied to clipboard!");
      })
      .catch(() => {
        this.tostr.error("Failed to copy. Try manually.");
      });
  }

  openDeleteServerModal() {
    this.closeServerMenu();
    this.showDeleteModal.set(true);
  }

  closeDeleteModal() {
    this.showDeleteModal.set(false);
    this.deleteConfirmation = '';
  }

  deleteServer() {
    if (!this.serverId.trim()) return;
    this.apiService.deleteServer(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.tostr.success("Server deleted successfully");
          this.router.navigate(['/app/friends/all']);
          this.deleteSeverEvent.notifyDeleteServer();
        },
        error: (err) => {
          this.tostr.error(err.error.error);
        }
      });
  }
}
