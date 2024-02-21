import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorVehiclesTableComponent } from './contractor-vehicles-table.component';

describe('ContractorVehiclesTableComponent', () => {
  let component: ContractorVehiclesTableComponent;
  let fixture: ComponentFixture<ContractorVehiclesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatSnackBarModule, MatDialogModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorVehiclesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
