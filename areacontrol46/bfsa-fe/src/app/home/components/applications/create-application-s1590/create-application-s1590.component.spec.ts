import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateApplicationS1590Component } from './create-application-s1590.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('CreateApplicationS1590Component', () => {
  let component: CreateApplicationS1590Component;
  let fixture: ComponentFixture<CreateApplicationS1590Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule],
      declarations: [CreateApplicationS1590Component],
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateApplicationS1590Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
