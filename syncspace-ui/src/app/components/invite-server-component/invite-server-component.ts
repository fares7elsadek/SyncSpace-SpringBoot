import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, Subject, takeUntil, timer } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { InviteModal } from '../../services/invite-modal';

interface ServerInviteInfo {
  name: string;
  iconUrl?: string;
  membersNumber:number
}

interface Star {
  x: number;
  y: number;
  size: number;
  delay: number;
}

interface Particle {
  x: number;
  delay: number;
}

@Component({
  selector: 'app-invite-server-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './invite-server-component.html',
  styleUrl: './invite-server-component.css'
})
export class InviteServerComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  
  // UI State
  isLoading = true;
  showInviteModal = false;
  isJoining = false;
  hasJoined = false;
  countdown = 3;
  
  // Data
  serverId: string = '';
  inviteCode: string = '';
  serverInfo: ServerInviteInfo | null = null;
  errorMessage: string = '';

  // Animation data
  stars: Star[] = [];
  particles: Particle[] = [];

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService,
    private toastr: ToastrService,
    private router: Router,
    private modal: InviteModal
  ) {
    this.generateStars();
    this.generateParticles();
  }

  ngOnInit(): void {
    combineLatest([
      this.route.paramMap,
      this.route.queryParamMap
    ]).pipe(takeUntil(this.destroy$))
    .subscribe(([params, queryParams]) => {
      this.serverId = params.get("serverId") || '';
      this.inviteCode = queryParams.get("code") || '';
      
      if (!this.serverId.trim() || !this.inviteCode.trim()) {
        this.handleError("Invalid invite link");
        return;
      }
      
      this.loadInviteInfo();
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private generateStars(): void {
    const starCount = 150;
    this.stars = [];
    
    for (let i = 0; i < starCount; i++) {
      this.stars.push({
        x: Math.random() * window.innerWidth,
        y: Math.random() * window.innerHeight,
        size: Math.random() * 3 + 1,
        delay: Math.random() * 3
      });
    }
  }

  private generateParticles(): void {
    const particleCount = 20;
    this.particles = [];
    
    for (let i = 0; i < particleCount; i++) {
      this.particles.push({
        x: Math.random() * 100,
        delay: Math.random() * 6
      });
    }
  }

  loadInviteInfo(): void {
    this.isLoading = true;
    
    this.apiService.getServer(this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.serverInfo = response.data;
          this.isLoading = false;
          this.showInviteModal = true;
        },
        error: (err) => {
          this.handleError(err.error?.error || "Failed to load invite information");
        }
      });
  }

  acceptInvite(): void {
    if (this.isJoining) return;
    
    this.isJoining = true;
    
    this.apiService.joinServer(this.inviteCode, this.serverId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.hasJoined = true;
          this.isJoining = false;
          this.toastr.success("Successfully joined the server!");
          this.startCountdown();
        },
        error: (err) => {
          this.isJoining = false;
          this.handleError("Failed to join server");
        }
      });
  }

  declineInvite(): void {
    this.router.navigate(['/app/friend/all']);
  }

  private startCountdown(): void {
    this.countdown = 3;
    
    timer(0, 1000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.countdown > 0) {
          this.countdown--;
        } else {
          this.router.navigate([`/app/server/${this.serverId}`]);
        }
      });
  }

  private handleError(message: string): void {
    this.errorMessage = message;
    this.isLoading = false;
    this.toastr.error(message);
    
    timer(5000)
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.router.navigate(['/app/friend/all']);
      });
  }

  navigateToServer(): void {
    this.router.navigate([`/app/server/${this.serverId}`]);
  }

  navigateToFriends(): void {
    this.router.navigate(['/app/friend/all']);
  }

  getServerInitials(name: string): string {
    if (!name) return 'S';
    
    const words = name.split(' ').filter(word => word.length > 0);
    if (words.length === 1) {
      return words[0].substring(0, 2).toUpperCase();
    }
    
    return words.slice(0, 2)
      .map(word => word.charAt(0).toUpperCase())
      .join('');
  }

  getCountdownProgress(): number {
    const circumference = 2 * Math.PI * 35; // radius = 35
    const progress = ((3 - this.countdown) / 3) * circumference;
    return circumference - progress;
  }
}