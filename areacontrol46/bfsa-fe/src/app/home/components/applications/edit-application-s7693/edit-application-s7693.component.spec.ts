import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS7693Component } from './edit-application-s7693.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

describe('EditApplicationS7693Component', () => {
  let component: EditApplicationS7693Component;
  let fixture: ComponentFixture<EditApplicationS7693Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS7693Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS7693Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
