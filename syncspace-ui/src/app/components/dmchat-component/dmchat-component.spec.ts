import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DMChatComponent } from './dmchat-component';

describe('DMChatComponent', () => {
  let component: DMChatComponent;
  let fixture: ComponentFixture<DMChatComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DMChatComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DMChatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
