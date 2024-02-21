import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ContractorDialogComponent } from './contractor-dialog.component';

describe('ContractorDialogComponent', () => {
  let component: ContractorDialogComponent;
  let fixture: ComponentFixture<ContractorDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [ContractorDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ContractorDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
