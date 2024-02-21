import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS3181Component } from './create-application-s3181.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS3181Component', () => {
  let component: CreateApplicationS3181Component;
  let fixture: ComponentFixture<CreateApplicationS3181Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS3181Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS3181Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
