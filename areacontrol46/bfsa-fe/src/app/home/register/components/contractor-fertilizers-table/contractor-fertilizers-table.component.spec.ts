import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContractorFertilizersTableComponent } from './contractor-fertilizers-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('ContractorFertilizersTableComponent', () => {
  let component: ContractorFertilizersTableComponent;
  let fixture: ComponentFixture<ContractorFertilizersTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ContractorFertilizersTableComponent ],
      imports: [NoopAnimationsModule, MatSnackBarModule, MatDialogModule],

    })
    .compileComponents();

    fixture = TestBed.createComponent(ContractorFertilizersTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
