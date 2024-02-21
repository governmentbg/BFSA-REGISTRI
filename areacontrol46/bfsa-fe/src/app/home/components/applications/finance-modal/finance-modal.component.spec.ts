import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FinanceModalComponent } from './finance-modal.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('FinanceModalComponent', () => {
  let component: FinanceModalComponent;
  let fixture: ComponentFixture<FinanceModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FinanceModalComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(FinanceModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
