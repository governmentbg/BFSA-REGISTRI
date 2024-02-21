import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-data-and-download-dialog',
  templateUrl: './data-and-download-dialog.component.html',
  styleUrls: ['./data-and-download-dialog.component.scss'],
})
export class DataAndDownloadDialogComponent {
  public regDate = new FormControl({ value: null, disabled: true });
  public regNumber = new FormControl({ value: null, disabled: true });

  constructor(
    public dialogRef: MatDialogRef<DataAndDownloadDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.regDate.setValue(this.data.regDate);
    this.regNumber.setValue(this.data.regNumber);
  }

  confirm() {
    //open next modal here
    this.dialogRef.close(true);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
