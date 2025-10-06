import { Component, OnDestroy, OnInit, signal, WritableSignal } from '@angular/core';
import { CreateServerRequest } from '../../models/api.model';
import { Subject, takeUntil } from 'rxjs';
import { ApiService } from '../../services/api.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ModalService } from '../../services/modal-service';
import { ServerEventsService } from '../../services/server-events-service';

@Component({
  selector: 'app-app-global-modal',
  imports: [CommonModule,FormsModule],
  templateUrl: './app-global-modal.html',
  styleUrl: './app-global-modal.css'
})
export class AppGlobalModal implements OnInit,OnDestroy {

  
  isCreatingServer:WritableSignal<boolean> = signal(false);
  
  newServer: CreateServerRequest = {
    name: '',
    description: '',
    isPublic: true
  };
  
  private destroy$ = new Subject<void>();

  constructor(
    private apiService: ApiService,
    private tostr:ToastrService,
    public modal:ModalService,
    private serverEvent:ServerEventsService
  ){}


  ngOnInit(): void {
    
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

 

  createServer() {
      if (!this.newServer.name.trim()) return;
      this.isCreatingServer.set(true)
      this.apiService.createServer(this.newServer)
       .pipe(takeUntil(this.destroy$))
       .subscribe({
        next:() =>{
          this.tostr.success("Server created successfully");
          this.serverEvent.notifyServerCreated();
          this.closeModal();
        },
        error:(err) =>{
          console.error('Failed to create server:', err);
          this.isCreatingServer.set(false);
          this.tostr.error(err.error.error);
        }
       })
  }

  closeModal() {
    this.modal.closeCreateServerModal();
    this.isCreatingServer.set(false);
    this.newServer = { name: '', description: '' , isPublic:false }; 
  }

}
