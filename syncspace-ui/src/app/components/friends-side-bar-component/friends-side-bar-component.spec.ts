import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FriendsSideBarComponent } from './friends-side-bar-component';

describe('FriendsSideBarComponent', () => {
  let component: FriendsSideBarComponent;
  let fixture: ComponentFixture<FriendsSideBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FriendsSideBarComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FriendsSideBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
