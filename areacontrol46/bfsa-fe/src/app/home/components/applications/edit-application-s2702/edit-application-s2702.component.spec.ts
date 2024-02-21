import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS2702Component } from './edit-application-s2702.component';
import { HttpClientModule } from '@angular/common/http';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('EditApplicationS2702Component', () => {
  let component: EditApplicationS2702Component;
  let fixture: ComponentFixture<EditApplicationS2702Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      imports: [HttpClientModule, MatSnackBarModule, MatDialogModule],

      declarations: [EditApplicationS2702Component],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS2702Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
