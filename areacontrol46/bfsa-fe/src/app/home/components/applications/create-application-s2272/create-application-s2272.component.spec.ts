import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS2272Component } from './create-application-s2272.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS2272Component', () => {
  let component: CreateApplicationS2272Component;
  let fixture: ComponentFixture<CreateApplicationS2272Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS2272Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS2272Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
