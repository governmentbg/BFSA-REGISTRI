import { Component, Inject } from '@angular/core';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { UntypedFormBuilder } from '@angular/forms';

@Component({
  selector: 'app-approval-order-date-dialog',
  templateUrl: './approval-order-date-dialog.component.html',
  styleUrls: ['./approval-order-date-dialog.component.scss'],
})
export class ApprovalOrderDateDialogComponent {
  public approvalForm = this.fb.group({
    orderNumber: [''],
    orderDate: [''],
  });
  constructor(
    public dialogRef: MatDialogRef<ApprovalDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder
  ) {}

  confirm() {
    //open next modal here
    this.dialogRef.close(this.approvalForm.value);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
