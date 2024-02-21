import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS3182Component } from './edit-application-s3182.component';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { DatePipe } from '@angular/common';

describe('EditApplicationS3182Component', () => {
  let component: EditApplicationS3182Component;
  let fixture: ComponentFixture<EditApplicationS3182Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS3182Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS3182Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
