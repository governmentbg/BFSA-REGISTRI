import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { RefusalDialogComponent } from './refusal-dialog.component';

describe('RefusalDialogComponent', () => {
  let component: RefusalDialogComponent;
  let fixture: ComponentFixture<RefusalDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [RefusalDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(RefusalDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
