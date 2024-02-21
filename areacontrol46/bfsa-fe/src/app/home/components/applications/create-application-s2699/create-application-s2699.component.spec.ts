import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS2699Component } from './create-application-s2699.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS2699Component', () => {
  let component: CreateApplicationS2699Component;
  let fixture: ComponentFixture<CreateApplicationS2699Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS2699Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS2699Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
