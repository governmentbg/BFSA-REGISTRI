import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorPppFacilitiesTableComponent } from './contractor-ppp-facilities-table.component';

describe('ContractorPppFacilitiesTableComponent', () => {
  let component: ContractorPppFacilitiesTableComponent;
  let fixture: ComponentFixture<ContractorPppFacilitiesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorPppFacilitiesTableComponent],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorPppFacilitiesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
