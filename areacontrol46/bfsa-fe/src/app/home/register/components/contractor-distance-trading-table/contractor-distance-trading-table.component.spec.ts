import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ContractorDistanceTradingTableComponent } from './contractor-distance-trading-table.component';

describe('ContractorDistanceTradingTableComponent', () => {
  let component: ContractorDistanceTradingTableComponent;
  let fixture: ComponentFixture<ContractorDistanceTradingTableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        NoopAnimationsModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    });
    fixture = TestBed.createComponent(ContractorDistanceTradingTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
