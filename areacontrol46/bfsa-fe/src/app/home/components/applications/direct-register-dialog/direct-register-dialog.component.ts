import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-direct-register-dialog',
  templateUrl: './direct-register-dialog.component.html',
  styleUrls: ['./direct-register-dialog.component.scss'],
})
export class DirectRegisterDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<DirectRegisterDialogComponent>,
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
