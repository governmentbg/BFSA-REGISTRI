import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS13661Component } from './edit-application-s13661.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

describe('EditApplicationS13661Component', () => {
  let component: EditApplicationS13661Component;
  let fixture: ComponentFixture<EditApplicationS13661Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS13661Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS13661Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
