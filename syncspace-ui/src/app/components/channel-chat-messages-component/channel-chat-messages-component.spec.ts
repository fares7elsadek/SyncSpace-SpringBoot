import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChannelChatMessagesComponent } from './channel-chat-messages-component';

describe('ChannelChatMessagesComponent', () => {
  let component: ChannelChatMessagesComponent;
  let fixture: ComponentFixture<ChannelChatMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChannelChatMessagesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChannelChatMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
