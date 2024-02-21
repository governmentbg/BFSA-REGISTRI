import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovalOrderDateDialogComponent } from './approval-order-date-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ApprovalOrderDateDialogComponent', () => {
  let component: ApprovalOrderDateDialogComponent;
  let fixture: ComponentFixture<ApprovalOrderDateDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ApprovalOrderDateDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ApprovalOrderDateDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
