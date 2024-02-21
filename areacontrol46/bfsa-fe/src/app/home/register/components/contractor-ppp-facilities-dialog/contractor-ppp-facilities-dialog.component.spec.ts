import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorPppFacilitiesDialogComponent } from './contractor-ppp-facilities-dialog.component';

describe('ContractorPppFacilitiesDialogComponent', () => {
  let component: ContractorPppFacilitiesDialogComponent;
  let fixture: ComponentFixture<ContractorPppFacilitiesDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorPppFacilitiesDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorPppFacilitiesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
