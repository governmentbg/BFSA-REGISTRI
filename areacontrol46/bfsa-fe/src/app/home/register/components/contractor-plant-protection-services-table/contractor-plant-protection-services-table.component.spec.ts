import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorPlantProtectionServicesTableComponent } from './contractor-plant-protection-services-table.component';

describe('ContractorPlantProtectionServicesTableComponent', () => {
  let component: ContractorPlantProtectionServicesTableComponent;
  let fixture: ComponentFixture<ContractorPlantProtectionServicesTableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractorPlantProtectionServicesTableComponent],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    });
    fixture = TestBed.createComponent(
      ContractorPlantProtectionServicesTableComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
