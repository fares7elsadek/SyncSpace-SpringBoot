import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-loading-spinner-component',
  imports: [CommonModule,FormsModule],
  templateUrl: './loading-spinner-component.html',
  styleUrl: './loading-spinner-component.css'
})
export class LoadingSpinnerComponent {
  @Input() isLoading = false;
  
  stars = Array.from({ length: 50 }, () => ({
    x: Math.random() * 100,
    y: Math.random() * 100,
    delay: Math.random() * 3
  }));
}
