import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExpertModalComponent } from './expert-modal.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ExpertModalComponent', () => {
  let component: ExpertModalComponent;
  let fixture: ComponentFixture<ExpertModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ExpertModalComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ExpertModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
