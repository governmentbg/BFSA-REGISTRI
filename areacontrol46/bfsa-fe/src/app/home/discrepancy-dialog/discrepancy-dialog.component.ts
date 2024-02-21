import { Component, Inject } from '@angular/core';
import { FormControl, UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-discrepancy-dialog',
  templateUrl: './discrepancy-dialog.component.html',
  styleUrls: ['./discrepancy-dialog.component.scss'],
})
export class DiscrepancyDialogComponent {
  public discrepancyForm = this.fb.group({
    discrepancyUntilDate: ['', [Validators.required]],
    description: ['', [Validators.required]],
    recordId: [this.data.recordId],
  });
  constructor(
    public dialogRef: MatDialogRef<DiscrepancyDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder
  ) {}

  fixDate() {
    this.discrepancyForm
      .get('discrepancyUntilDate')
      ?.setValue(
        this.addDays(
          this.discrepancyForm.get('discrepancyUntilDate')?.value,
          1
        )
          .toISOString()
          .slice(0, 10),
        { emitEvent: false }
      );
  }

  addDays(date: Date, days: number) {
    let result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  confirm() {
    this.fixDate()
    this.dialogRef.close(this.discrepancyForm.value);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
