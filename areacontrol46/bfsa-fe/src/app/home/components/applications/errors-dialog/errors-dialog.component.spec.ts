import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorsDialogComponent } from './errors-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ErrorsDialogComponent', () => {
  let component: ErrorsDialogComponent;
  let fixture: ComponentFixture<ErrorsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [ErrorsDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
