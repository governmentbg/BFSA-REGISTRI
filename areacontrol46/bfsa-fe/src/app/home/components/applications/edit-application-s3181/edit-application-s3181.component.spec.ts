import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS3181Component } from './edit-application-s3181.component';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { DatePipe } from '@angular/common';

describe('EditApplicationS3181Component', () => {
  let component: EditApplicationS3181Component;
  let fixture: ComponentFixture<EditApplicationS3181Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
      declarations: [EditApplicationS3181Component],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS3181Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
