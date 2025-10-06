import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InviteServerComponent } from './invite-server-component';

describe('InviteServerComponent', () => {
  let component: InviteServerComponent;
  let fixture: ComponentFixture<InviteServerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InviteServerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InviteServerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
