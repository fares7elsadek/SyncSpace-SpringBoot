import { TestBed } from '@angular/core/testing';

import { NotificationIncomingEvent } from './notification-incoming-event';

describe('NotificationIncomingEvent', () => {
  let service: NotificationIncomingEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationIncomingEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
