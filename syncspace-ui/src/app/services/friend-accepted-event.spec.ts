import { TestBed } from '@angular/core/testing';

import { FriendAcceptedEvent } from './friend-accepted-event';

describe('FriendAcceptedEvent', () => {
  let service: FriendAcceptedEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FriendAcceptedEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
