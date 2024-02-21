import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS503Component } from './edit-application-s503.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

describe('EditApplicationS503Component', () => {
  let component: EditApplicationS503Component;
  let fixture: ComponentFixture<EditApplicationS503Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS503Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS503Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
