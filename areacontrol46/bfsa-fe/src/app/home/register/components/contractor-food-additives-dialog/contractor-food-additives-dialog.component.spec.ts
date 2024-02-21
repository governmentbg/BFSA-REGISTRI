import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorFoodAdditivesDialogComponent } from './contractor-food-additives-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ContractorFoodAdditivesDialogComponent', () => {
  let component: ContractorFoodAdditivesDialogComponent;
  let fixture: ComponentFixture<ContractorFoodAdditivesDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContractorFoodAdditivesDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [ NoopAnimationsModule],
    });
    fixture = TestBed.createComponent(ContractorFoodAdditivesDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
