import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorFoodAdditivesTableComponent } from './contractor-food-additives-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ContractorFoodAdditivesTableComponent', () => {
  let component: ContractorFoodAdditivesTableComponent;
  let fixture: ComponentFixture<ContractorFoodAdditivesTableComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractorFoodAdditivesTableComponent],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],
    });
    fixture = TestBed.createComponent(ContractorFoodAdditivesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
