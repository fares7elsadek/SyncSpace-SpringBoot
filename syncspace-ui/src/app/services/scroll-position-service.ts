import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';


interface ScrollPosition {
  channelId: string;
  scrollTop: number;
  scrollHeight: number;
  timestamp: number;
}


@Injectable({
  providedIn: 'root'
})
export class ScrollPositionService {
  private scrollPositions = new Map<string, ScrollPosition>();
  private scrollStateSubject = new BehaviorSubject<boolean>(true);
  
  // Observable to track if user should auto-scroll
  public shouldAutoScroll$: Observable<boolean> = this.scrollStateSubject.asObservable();

  constructor() {}

  // Save scroll position for a channel
  saveScrollPosition(channelId: string, scrollTop: number, scrollHeight: number): void {
    this.scrollPositions.set(channelId, {
      channelId,
      scrollTop,
      scrollHeight,
      timestamp: Date.now()
    });
  }

  // Get saved scroll position for a channel
  getScrollPosition(channelId: string): ScrollPosition | null {
    const position = this.scrollPositions.get(channelId);
    
    // Return position if it's less than 5 minutes old
    if (position && (Date.now() - position.timestamp) < 5 * 60 * 1000) {
      return position;
    }
    
    // Clean up old position
    if (position) {
      this.scrollPositions.delete(channelId);
    }
    
    return null;
  }

  // Clear scroll position for a channel
  clearScrollPosition(channelId: string): void {
    this.scrollPositions.delete(channelId);
  }

  // Update auto-scroll state
  setAutoScrollState(shouldAutoScroll: boolean): void {
    this.scrollStateSubject.next(shouldAutoScroll);
  }

  // Check if element is scrolled to bottom
  isScrolledToBottom(element: HTMLElement, threshold: number = 10): boolean {
    const { scrollTop, scrollHeight, clientHeight } = element;
    return scrollHeight - scrollTop - clientHeight < threshold;
  }

  // Smooth scroll to bottom
  scrollToBottom(element: HTMLElement, smooth: boolean = true): Promise<void> {
    return new Promise((resolve) => {
      const startScrollTop = element.scrollTop;
      const targetScrollTop = element.scrollHeight - element.clientHeight;
      
      if (!smooth || Math.abs(targetScrollTop - startScrollTop) < 100) {
        element.scrollTop = targetScrollTop;
        resolve();
        return;
      }

      const duration = 300;
      const startTime = performance.now();
      
      const animateScroll = (currentTime: number) => {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        // Easing function for smooth animation
        const easeOutCubic = 1 - Math.pow(1 - progress, 3);
        
        element.scrollTop = startScrollTop + (targetScrollTop - startScrollTop) * easeOutCubic;
        
        if (progress < 1) {
          requestAnimationFrame(animateScroll);
        } else {
          resolve();
        }
      };
      
      requestAnimationFrame(animateScroll);
    });
  }

  // Maintain scroll position when new content is added at the top
  maintainScrollPosition(element: HTMLElement, previousHeight: number): void {
    const newHeight = element.scrollHeight;
    const heightDifference = newHeight - previousHeight;
    
    if (heightDifference > 0) {
      element.scrollTop += heightDifference;
    }
  }

  // Calculate if more content should be loaded based on scroll position
  shouldLoadMore(element: HTMLElement, threshold: number = 100): boolean {
    return element.scrollTop < threshold;
  }

  // Debounce scroll events for performance
  debounceScroll(callback: () => void, delay: number = 100): () => void {
    let timeoutId: number;
    
    return () => {
      clearTimeout(timeoutId);
      timeoutId = window.setTimeout(callback, delay);
    };
  }

  // Get visible message range based on viewport
  getVisibleMessageRange(element: HTMLElement, messageElements: HTMLElement[]): { start: number; end: number } {
    const containerRect = element.getBoundingClientRect();
    const containerTop = containerRect.top;
    const containerBottom = containerRect.bottom;
    
    let start = -1;
    let end = -1;
    
    messageElements.forEach((messageEl, index) => {
      const messageRect = messageEl.getBoundingClientRect();
      const messageTop = messageRect.top;
      const messageBottom = messageRect.bottom;
      
      // Check if message is visible in viewport
      if (messageBottom > containerTop && messageTop < containerBottom) {
        if (start === -1) start = index;
        end = index;
      }
    });
    
    return { start: Math.max(0, start), end: Math.min(messageElements.length - 1, end) };
  }

  // Clean up old scroll positions (call this periodically)
  cleanupOldPositions(): void {
    const now = Date.now();
    const maxAge = 10 * 60 * 1000; // 10 minutes
    
    for (const [channelId, position] of this.scrollPositions.entries()) {
      if (now - position.timestamp > maxAge) {
        this.scrollPositions.delete(channelId);
      }
    }
  }
}
