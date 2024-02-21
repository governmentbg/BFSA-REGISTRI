import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { InspectionConfirmDialogComponent } from './inspection-confirm-dialog.component';

describe('InspectionConfirmDialogComponent', () => {
  let component: InspectionConfirmDialogComponent;
  let fixture: ComponentFixture<InspectionConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [InspectionConfirmDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(InspectionConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
