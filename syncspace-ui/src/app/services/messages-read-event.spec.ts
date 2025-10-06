import { TestBed } from '@angular/core/testing';

import { MessagesReadEvent } from './messages-read-event';

describe('MessagesReadEvent', () => {
  let service: MessagesReadEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MessagesReadEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
