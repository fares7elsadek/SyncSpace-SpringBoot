import { TestBed } from '@angular/core/testing';

import { RoomConnectionEvent } from './room-connection-event';

describe('RoomConnectionEvent', () => {
  let service: RoomConnectionEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RoomConnectionEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
