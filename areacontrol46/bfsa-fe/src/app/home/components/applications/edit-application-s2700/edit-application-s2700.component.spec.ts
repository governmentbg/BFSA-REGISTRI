import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS2700Component } from './edit-application-s2700.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS2700Component', () => {
  let component: EditApplicationS2700Component;
  let fixture: ComponentFixture<EditApplicationS2700Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS2700Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS2700Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
