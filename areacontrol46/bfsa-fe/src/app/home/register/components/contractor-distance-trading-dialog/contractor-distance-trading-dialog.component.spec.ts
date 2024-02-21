import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorDistanceTradingDialogComponent } from './contractor-distance-trading-dialog.component';

describe('ContractorDistanceTradingDialogComponent', () => {
  let component: ContractorDistanceTradingDialogComponent;
  let fixture: ComponentFixture<ContractorDistanceTradingDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule],
    });
    fixture = TestBed.createComponent(ContractorDistanceTradingDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
