import { TestBed } from '@angular/core/testing';

import { DeleteServerEvent } from './delete-server-event';

describe('DeleteServerEvent', () => {
  let service: DeleteServerEvent;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DeleteServerEvent);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
