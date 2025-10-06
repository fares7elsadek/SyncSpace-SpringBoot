import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RoomChannelComponent } from './room-channel-component';

describe('RoomChannelComponent', () => {
  let component: RoomChannelComponent;
  let fixture: ComponentFixture<RoomChannelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RoomChannelComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RoomChannelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
