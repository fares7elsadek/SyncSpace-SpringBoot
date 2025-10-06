import { TestBed } from '@angular/core/testing';

import { SendMessageEvent } from './send-message-event';

describe('SendMessageEvent', () => {
  let service: SendMessageEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SendMessageEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
