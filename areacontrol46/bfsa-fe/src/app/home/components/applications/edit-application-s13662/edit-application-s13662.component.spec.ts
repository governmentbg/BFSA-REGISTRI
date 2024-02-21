import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS13662Component } from './edit-application-s13662.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

describe('EditApplicationS13662Component', () => {
  let component: EditApplicationS13662Component;
  let fixture: ComponentFixture<EditApplicationS13662Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS13662Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS13662Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
