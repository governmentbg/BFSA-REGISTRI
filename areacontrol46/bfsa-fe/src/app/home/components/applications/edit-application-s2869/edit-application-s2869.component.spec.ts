import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditApplicationS2869Component } from './edit-application-s2869.component';
import { DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('EditApplicationS2869Component', () => {
  let component: EditApplicationS2869Component;
  let fixture: ComponentFixture<EditApplicationS2869Component>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EditApplicationS2869Component],
      imports: [HttpClientModule, MatDialogModule],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
        DatePipe,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditApplicationS2869Component);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
