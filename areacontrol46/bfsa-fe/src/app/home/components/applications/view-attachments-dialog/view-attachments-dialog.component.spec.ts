import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { InspectionService } from 'src/app/home/inspections/services/inspection.service';

import { ViewAttachmentsDialogComponent } from './view-attachments-dialog.component';

describe('ViewAttachmentsDialogComponent', () => {
  let component: ViewAttachmentsDialogComponent;
  let fixture: ComponentFixture<ViewAttachmentsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ViewAttachmentsDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ViewAttachmentsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
