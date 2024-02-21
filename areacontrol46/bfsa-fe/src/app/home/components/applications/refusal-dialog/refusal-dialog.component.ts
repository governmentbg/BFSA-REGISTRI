import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-refusal-dialog',
  templateUrl: './refusal-dialog.component.html',
  styleUrls: ['./refusal-dialog.component.scss'],
})
export class RefusalDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<RefusalDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  confirm() {
	//open next modal here
	this.dialogRef.close(true)
  }

  discard() {
    this.dialogRef.close(false);
  }
}
