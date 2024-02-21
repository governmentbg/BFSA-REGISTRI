import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdjuvantDialogComponent } from './adjuvant-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('AdjuvantDialogComponent', () => {
  let component: AdjuvantDialogComponent;
  let fixture: ComponentFixture<AdjuvantDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdjuvantDialogComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdjuvantDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
