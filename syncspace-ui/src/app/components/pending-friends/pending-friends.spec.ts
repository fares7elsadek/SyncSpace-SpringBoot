import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingFriends } from './pending-friends';

describe('PendingFriends', () => {
  let component: PendingFriends;
  let fixture: ComponentFixture<PendingFriends>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingFriends]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingFriends);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
