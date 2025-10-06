import { TestBed } from '@angular/core/testing';

import { NewMemberEvent } from './new-member-event';

describe('NewMemberEvent', () => {
  let service: NewMemberEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NewMemberEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
