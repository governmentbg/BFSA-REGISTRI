import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { PrintDocumentDialogComponent } from './print-document-dialog.component';

describe('PrintDocumentDialogComponent', () => {
  let component: PrintDocumentDialogComponent;
  let fixture: ComponentFixture<PrintDocumentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [PrintDocumentDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PrintDocumentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
