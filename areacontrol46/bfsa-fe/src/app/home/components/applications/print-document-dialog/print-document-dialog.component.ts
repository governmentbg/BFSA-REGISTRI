import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-print-document-dialog',
  templateUrl: './print-document-dialog.component.html',
  styleUrls: ['./print-document-dialog.component.scss'],
})
export class PrintDocumentDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<PrintDocumentDialogComponent>,
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
