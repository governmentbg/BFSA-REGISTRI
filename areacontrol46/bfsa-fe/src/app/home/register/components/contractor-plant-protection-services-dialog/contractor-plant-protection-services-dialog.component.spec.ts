import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorPlantProtectionServicesDialogComponent } from './contractor-plant-protection-services-dialog.component';

describe('ContractorPlantProtectionServicesDialogComponent', () => {
  let component: ContractorPlantProtectionServicesDialogComponent;
  let fixture: ComponentFixture<ContractorPlantProtectionServicesDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractorPlantProtectionServicesDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [NoopAnimationsModule],
    });
    fixture = TestBed.createComponent(
      ContractorPlantProtectionServicesDialogComponent
    );
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
