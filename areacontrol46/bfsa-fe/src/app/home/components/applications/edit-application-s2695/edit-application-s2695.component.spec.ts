import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS2695Component } from './edit-application-s2695.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS2695Component', () => {
  let component: EditApplicationS2695Component;
  let fixture: ComponentFixture<EditApplicationS2695Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS2695Component],
      imports: [HttpClientModule, MatDialogModule],

      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS2695Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
