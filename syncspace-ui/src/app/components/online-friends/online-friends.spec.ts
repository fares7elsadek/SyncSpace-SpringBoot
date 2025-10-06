import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OnlineFriends } from './online-friends';

describe('OnlineFriends', () => {
  let component: OnlineFriends;
  let fixture: ComponentFixture<OnlineFriends>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OnlineFriends]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OnlineFriends);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
