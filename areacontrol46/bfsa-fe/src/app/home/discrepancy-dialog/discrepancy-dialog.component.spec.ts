import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DiscrepancyDialogComponent } from './discrepancy-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('DiscrepancyDialogComponent', () => {
  let component: DiscrepancyDialogComponent;
  let fixture: ComponentFixture<DiscrepancyDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DiscrepancyDialogComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(DiscrepancyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
