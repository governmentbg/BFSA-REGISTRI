import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { CreateApplicationS3180Component } from './create-application-s3180.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS3180Component', () => {
  let component: CreateApplicationS3180Component;
  let fixture: ComponentFixture<CreateApplicationS3180Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS3180Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS3180Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
