import { Component, Inject } from '@angular/core';
import { FormControl, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-for-correction-dialog',
  templateUrl: './for-correction-dialog.component.html',
  styleUrls: ['./for-correction-dialog.component.scss'],
})
export class ForCorrectionDialogComponent {
  public correctionForm = this.fb.group({
    description: ['', Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ForCorrectionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  confirm() {
    //open next modal here
    this.dialogRef.close(this.correctionForm.get('description')?.value);
  }

  discard() {
    this.dialogRef.close(false);
  }
}
