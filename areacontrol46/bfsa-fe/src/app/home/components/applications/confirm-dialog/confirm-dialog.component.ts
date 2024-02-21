import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ExpertModalComponent } from '../expert-modal/expert-modal.component';
import { FormControl } from '@angular/forms';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss'],
})
export class ConfirmDialogComponent {
  public recordPrice = new FormControl();

  constructor(
    public dialogRef: MatDialogRef<ExpertModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  confirm() {
    //open next modal here
    this.dialogRef.close(this.recordPrice);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
