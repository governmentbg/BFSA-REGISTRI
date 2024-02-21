import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { InspectionDetailsComponent } from './inspection-details.component';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('InspectionDetailsComponent', () => {
  let component: InspectionDetailsComponent;
  let fixture: ComponentFixture<InspectionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InspectionDetailsComponent],
      imports: [
        HttpClientModule,
        NoopAnimationsModule,
        RouterTestingModule,
        MatDatepickerModule,
        MatDialogModule,
        MatNativeDateModule,
        MatSnackBarModule,
      ],
      providers: [
        MatDatepickerModule,
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(InspectionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
