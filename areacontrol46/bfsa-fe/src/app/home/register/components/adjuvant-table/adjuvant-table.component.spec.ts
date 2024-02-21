import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdjuvantTableComponent } from './adjuvant-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';

describe('AdjuvantTableComponent', () => {
  let component: AdjuvantTableComponent;
  let fixture: ComponentFixture<AdjuvantTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatDialogModule, MatSnackBarModule],
    }).compileComponents();

    fixture = TestBed.createComponent(AdjuvantTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
