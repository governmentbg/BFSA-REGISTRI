import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialogModule,
} from '@angular/material/dialog';
import { EditApplicationS2701Component } from './edit-application-s2701.component';
import { HttpClientModule } from '@angular/common/http';
import { DatePipe } from '@angular/common';

describe('EditApplicationS2701Component', () => {
  let component: EditApplicationS2701Component;
  let fixture: ComponentFixture<EditApplicationS2701Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, MatDialogModule],
      declarations: [EditApplicationS2701Component],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS2701Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
