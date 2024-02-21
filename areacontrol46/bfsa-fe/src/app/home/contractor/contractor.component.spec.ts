import { HttpClientModule } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { ContractorListComponent } from './components/contractor-list/contractor-list.component';

import { ContractorComponent } from './contractor.component';

describe('ContractorComponent', () => {
  let component: ContractorComponent;
  let fixture: ComponentFixture<ContractorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContractorComponent, ContractorListComponent],
      imports: [HttpClientModule, MatDialogModule, MatSnackBarModule],
      schemas: [NO_ERRORS_SCHEMA],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
