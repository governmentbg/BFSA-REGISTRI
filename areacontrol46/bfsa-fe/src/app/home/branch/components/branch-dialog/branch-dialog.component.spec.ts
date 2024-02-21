import { HttpClientModule } from '@angular/common/http';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { BranchDialogComponent } from './branch-dialog.component';

describe('BranchDialogComponent', () => {
  let component: BranchDialogComponent;
  let fixture: ComponentFixture<BranchDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatAutocompleteModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [BranchDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BranchDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
