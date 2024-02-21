import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS3201Component } from './edit-application-s3201.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS3201Component', () => {
  let component: EditApplicationS3201Component;
  let fixture: ComponentFixture<EditApplicationS3201Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS3201Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditApplicationS3201Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
