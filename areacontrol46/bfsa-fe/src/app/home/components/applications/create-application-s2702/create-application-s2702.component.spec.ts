import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS2702Component } from './create-application-s2702.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS2702Component', () => {
  let component: CreateApplicationS2702Component;
  let fixture: ComponentFixture<CreateApplicationS2702Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS2702Component],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS2702Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
