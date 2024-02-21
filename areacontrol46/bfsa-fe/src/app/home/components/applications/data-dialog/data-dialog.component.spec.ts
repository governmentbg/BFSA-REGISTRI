import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { DataDialogComponent } from './data-dialog.component';

describe('DataDialogComponent', () => {
  let component: DataDialogComponent;
  let fixture: ComponentFixture<DataDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DataDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DataDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
