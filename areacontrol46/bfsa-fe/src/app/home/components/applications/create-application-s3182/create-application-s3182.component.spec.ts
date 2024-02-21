import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS3182Component } from './create-application-s3182.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS3182Component', () => {
  let component: CreateApplicationS3182Component;
  let fixture: ComponentFixture<CreateApplicationS3182Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CreateApplicationS3182Component],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS3182Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
