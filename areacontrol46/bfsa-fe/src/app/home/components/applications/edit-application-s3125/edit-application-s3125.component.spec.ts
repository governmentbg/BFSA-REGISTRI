import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS3125Component } from './edit-application-s3125.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS3125Component', () => {
  let component: EditApplicationS3125Component;
  let fixture: ComponentFixture<EditApplicationS3125Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditApplicationS3125Component ],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditApplicationS3125Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
