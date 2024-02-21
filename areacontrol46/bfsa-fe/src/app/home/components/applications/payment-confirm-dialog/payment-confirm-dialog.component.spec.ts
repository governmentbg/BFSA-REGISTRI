import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentConfirmDialogComponent } from './payment-confirm-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

describe('PaymentConfirmDialogComponent', () => {
  let component: PaymentConfirmDialogComponent;
  let fixture: ComponentFixture<PaymentConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaymentConfirmDialogComponent ],
      providers: [
        { provide: MatDialogRef, useValue: {} },
        { provide: MAT_DIALOG_DATA, useValue: [] },
      ],
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaymentConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
