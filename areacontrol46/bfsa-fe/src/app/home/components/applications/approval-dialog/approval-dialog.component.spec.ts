import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ApprovalDialogComponent } from './approval-dialog.component';

describe('ApprovalDialogComponent', () => {
  let component: ApprovalDialogComponent;
  let fixture: ComponentFixture<ApprovalDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [ApprovalDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ApprovalDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
