import { Component, Inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ExpertModalComponent } from '../expert-modal/expert-modal.component';

@Component({
  selector: 'app-finance-modal',
  templateUrl: './finance-modal.component.html',
  styleUrls: ['./finance-modal.component.scss'],
})
export class FinanceModalComponent {
  public amount = new FormControl({value: parseFloat(this.data).toFixed(2), disabled: true});
  constructor(
    public dialogRef: MatDialogRef<ExpertModalComponent>,
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
