import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS1199Component } from './edit-application-s1199.component';
import { HttpClientModule } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS1199Component', () => {
  let component: EditApplicationS1199Component;
  let fixture: ComponentFixture<EditApplicationS1199Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditApplicationS1199Component ],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditApplicationS1199Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
