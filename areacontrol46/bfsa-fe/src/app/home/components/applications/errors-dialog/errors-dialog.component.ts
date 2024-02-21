import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-errors-dialog',
  templateUrl: './errors-dialog.component.html',
  styleUrls: ['./errors-dialog.component.scss'],
})
export class ErrorsDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ErrorsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  confirm() {
    //open next modal here
    this.dialogRef.close(true);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
