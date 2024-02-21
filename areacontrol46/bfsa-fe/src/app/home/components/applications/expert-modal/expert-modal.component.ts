import { Component, Inject } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-expert-modal',
  templateUrl: './expert-modal.component.html',
  styleUrls: ['./expert-modal.component.scss'],
})
export class ExpertModalComponent {
  public amount = new FormControl(
    '',
    // Validators.pattern('[0-9]+[.]{1}[0-9]{2}')
  );
  constructor(
    public dialogRef: MatDialogRef<ExpertModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    if (this.data) {
      this.amount.setValue(parseFloat(this.data).toFixed(2));
      this.amount.disable();
    }
  }

  confirm() {
    //open next modal here
    this.dialogRef.close(this.amount.value);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
