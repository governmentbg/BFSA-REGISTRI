import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-inspection-confirm-dialog',
  templateUrl: './inspection-confirm-dialog.component.html',
  styleUrls: ['./inspection-confirm-dialog.component.scss'],
})
export class InspectionConfirmDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<InspectionConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string }
  ) {
    dialogRef.disableClose = true;
  }
}
