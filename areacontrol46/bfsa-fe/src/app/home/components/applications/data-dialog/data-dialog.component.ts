import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';

@Component({
  selector: 'app-data-dialog',
  templateUrl: './data-dialog.component.html',
  styleUrls: ['./data-dialog.component.scss'],
})
export class DataDialogComponent {
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
