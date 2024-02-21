import { Component, Inject } from '@angular/core';
import { Validators, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { applicationStatus } from 'src/app/enums/applicationStatus';

@Component({
  selector: 'app-contractor-seeds-dialog',
  templateUrl: './contractor-seeds-dialog.component.html',
  styleUrls: ['./contractor-seeds-dialog.component.scss'],
})
export class ContractorSeedsDialogComponent {
  public applicationStatuses = applicationStatus;

  public dialogForm = this.fb.group({
    regNumber: [null, Validators.required],
    regDate: [null, Validators.required],
    seedName: [null, Validators.required],
    seedQuantity: [null, Validators.required],
    seedTradeName: [null, Validators.required],
    applicationStatus: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorSeedsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data) {
      this.dialogForm.patchValue({
        regNumber: this.data.regNumber ?? 'n/a',
        regDate: this.data.regDate ?? 'n/a',
        seedName: this.data.seedName ?? 'n/a',
        seedQuantity: this.data.seedQuantity ?? 'n/a',
        seedTradeName: this.data.seedTradeName ?? 'n/a',
        applicationStatus: this.data.applicationStatus ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
