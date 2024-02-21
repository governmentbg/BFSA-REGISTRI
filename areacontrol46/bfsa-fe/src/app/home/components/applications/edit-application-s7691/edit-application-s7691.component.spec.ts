import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS7691Component } from './edit-application-s7691.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS7691Component', () => {
  let component: EditApplicationS7691Component;
  let fixture: ComponentFixture<EditApplicationS7691Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditApplicationS7691Component ],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditApplicationS7691Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
