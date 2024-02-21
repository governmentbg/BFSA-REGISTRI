import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-payment-confirm-dialog',
  templateUrl: './payment-confirm-dialog.component.html',
  styleUrls: ['./payment-confirm-dialog.component.scss'],
})
export class PaymentConfirmDialogComponent {
  public amount = new FormControl();

  constructor(
    public dialogRef: MatDialogRef<PaymentConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  confirm() {
    this.dialogRef.close(this.amount.value);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
