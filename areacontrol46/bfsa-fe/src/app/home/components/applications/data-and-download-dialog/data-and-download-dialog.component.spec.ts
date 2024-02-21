import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataAndDownloadDialogComponent } from './data-and-download-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('DataAndDownloadDialogComponent', () => {
  let component: DataAndDownloadDialogComponent;
  let fixture: ComponentFixture<DataAndDownloadDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DataAndDownloadDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(DataAndDownloadDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
