import { TestBed } from '@angular/core/testing';

import { InviteModal } from './invite-modal';

describe('InviteModal', () => {
  let service: InviteModal;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InviteModal);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
