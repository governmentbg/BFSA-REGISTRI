import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { DirectRegisterDialogComponent } from './direct-register-dialog.component';

describe('DirectRegisterDialogComponent', () => {
  let component: DirectRegisterDialogComponent;
  let fixture: ComponentFixture<DirectRegisterDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
      declarations: [DirectRegisterDialogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(DirectRegisterDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
