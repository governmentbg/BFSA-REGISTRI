import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS1811Component } from './edit-application-s1811.component';
import { EditApplicationS2272Component } from '../edit-application-s2272/edit-application-s2272.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS1811Component', () => {
  let component: EditApplicationS1811Component;
  let fixture: ComponentFixture<EditApplicationS1811Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatDialogModule],
      declarations: [EditApplicationS2272Component],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditApplicationS1811Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
