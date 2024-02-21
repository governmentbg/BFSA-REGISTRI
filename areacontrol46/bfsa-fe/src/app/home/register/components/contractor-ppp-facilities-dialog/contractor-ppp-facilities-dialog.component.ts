import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-contractor-ppp-facilities-dialog',
  templateUrl: './contractor-ppp-facilities-dialog.component.html',
  styleUrls: ['./contractor-ppp-facilities-dialog.component.scss'],
})
export class ContractorPppFacilitiesDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    legalActNumber: [null, Validators.required],
    legalActType: [null, Validators.required],
    objectType: [null, Validators.required],
    address: [null, Validators.required],
    person: [null, Validators.required],
    identifier: [null, Validators.required],
    status: [null, Validators.required],
  });
  facilityStatusObj: (string | FacilityStatus)[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorPppFacilitiesDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { item: any }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data.item) {
      this.dialogForm.patchValue({
        legalActNumber: this.data.item.legalActNumber ?? 'n/a',
        legalActType: this.data.item.legalActNumber ?? 'n/a',
        objectType: this.data.item.name ?? 'n/a',
        address: this.data.item.address ?? 'n/a',
        person: this.data.item.activityTypeName ?? 'n/a',
        identifier: this.data.item.facilityTypeName ?? 'n/a',
        status: this.data.item.status,
      });
    }
    this.facilityStatusObj = Object.values(FacilityStatus).filter(
      (value) => typeof value === 'string'
    );
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
