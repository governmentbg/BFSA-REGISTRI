import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { ContractorFacilitiesTableComponent } from './contractor-facilities-table.component';
import { HttpClientModule } from '@angular/common/http';

describe('ContractorFacilitiesTableComponent', () => {
  let component: ContractorFacilitiesTableComponent;
  let fixture: ComponentFixture<ContractorFacilitiesTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorFacilitiesTableComponent],
      imports: [
        NoopAnimationsModule,
        MatSnackBarModule,
        MatDialogModule,
        HttpClientModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorFacilitiesTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
