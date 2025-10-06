import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModalService {
   // Signal for create server modal visibility
  showCreateServerModal = signal(false);

  openCreateServerModal() {
    this.showCreateServerModal.set(true);
  }

  closeCreateServerModal() {
    this.showCreateServerModal.set(false);
  }
}
