import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ForCorrectionDialogComponent } from './for-correction-dialog.component';

describe('ForCorrectionDialogComponent', () => {
  let component: ForCorrectionDialogComponent;
  let fixture: ComponentFixture<ForCorrectionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [ForCorrectionDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ForCorrectionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
