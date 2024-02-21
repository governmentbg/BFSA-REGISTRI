import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  MatDialogModule,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';

import { EditApplicationS3180Component } from './edit-application-s3180.component';
import { DatePipe } from '@angular/common';

describe('EditApplicationS3180Component', () => {
  let component: EditApplicationS3180Component;
  let fixture: ComponentFixture<EditApplicationS3180Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
      imports: [HttpClientModule, MatDialogModule],
      declarations: [EditApplicationS3180Component],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS3180Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
