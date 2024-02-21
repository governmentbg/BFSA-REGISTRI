import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-contractor-distance-trading-dialog',
  templateUrl: './contractor-distance-trading-dialog.component.html',
  styleUrls: ['./contractor-distance-trading-dialog.component.scss'],
})
export class ContractorDistanceTradingDialogComponent implements OnInit {
  public facilityStatusList: FacilityStatus;
  dialogForm = this.fb.group({
    applicationNumber: [null, Validators.required],
    phone: [null, Validators.required],
    mail: [null, Validators.required],
    url: [null, Validators.required],
    status: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorDistanceTradingDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: any
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data) {
      this.dialogForm.patchValue({
        applicationNumber: this.data.applicationNumber ?? 'n/a',
        phone: this.data.phone ?? 'n/a',
        mail: this.data.mail ?? 'n/a',
        url: this.data.url ?? 'n/a',
        status: this.data.status ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
