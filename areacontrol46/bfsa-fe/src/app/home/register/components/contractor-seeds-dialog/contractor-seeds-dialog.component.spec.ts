import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorSeedsDialogComponent } from './contractor-seeds-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ContractorSeedsDialogComponent', () => {
  let component: ContractorSeedsDialogComponent;
  let fixture: ComponentFixture<ContractorSeedsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorSeedsDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorSeedsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
