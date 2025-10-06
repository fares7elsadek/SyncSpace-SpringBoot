import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MessageComposer } from './message-composer';

describe('MessageComposer', () => {
  let component: MessageComposer;
  let fixture: ComponentFixture<MessageComposer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MessageComposer]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MessageComposer);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
