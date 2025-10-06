import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppGlobalModal } from './app-global-modal';

describe('AppGlobalModal', () => {
  let component: AppGlobalModal;
  let fixture: ComponentFixture<AppGlobalModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppGlobalModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AppGlobalModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
