import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS1590Component } from './edit-application-s1590.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

describe('EditApplicationS1590Component', () => {
  let component: EditApplicationS1590Component;
  let fixture: ComponentFixture<EditApplicationS1590Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatDialogModule],
      declarations: [EditApplicationS1590Component],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS1590Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
