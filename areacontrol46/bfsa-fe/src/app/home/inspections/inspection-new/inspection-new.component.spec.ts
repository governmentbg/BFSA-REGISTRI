import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { InspectionNewComponent } from './inspection-new.component';

describe('InspectionNewComponent', () => {
  let component: InspectionNewComponent;
  let fixture: ComponentFixture<InspectionNewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InspectionNewComponent],
      imports: [
        HttpClientModule,
        NoopAnimationsModule,
        MatDatepickerModule,
        MatNativeDateModule,
        RouterTestingModule,
        MatSnackBarModule,
      ],
      providers: [MatDatepickerModule],
    }).compileComponents();

    fixture = TestBed.createComponent(InspectionNewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
