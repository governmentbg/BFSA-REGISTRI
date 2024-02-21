import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorFertilizersDialogComponent } from './contractor-fertilizers-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('ContractorFertilizersDialogComponent', () => {
  let component: ContractorFertilizersDialogComponent;
  let fixture: ComponentFixture<ContractorFertilizersDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContractorFertilizersDialogComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractorFertilizersDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
