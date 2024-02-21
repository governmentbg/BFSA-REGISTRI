import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-contractor-plant-protection-services-dialog',
  templateUrl: './contractor-plant-protection-services-dialog.component.html',
  styleUrls: ['./contractor-plant-protection-services-dialog.component.scss'],
})
export class ContractorPlantProtectionServicesDialogComponent
  implements OnInit
{
  public facilityStatusList = Object.entries(FacilityStatus);

  dialogForm = this.fb.group({
    notificationNumber: [null, Validators.required],
    address: [null, Validators.required],
    fullAddress: [null, Validators.required],
    ch83CertifiedPerson: [null, Validators.required],
    status: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorPlantProtectionServicesDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: any
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.facilityStatusList);

    if (this.data) {
      this.dialogForm.patchValue({
        notificationNumber: this.data.notificationNumber ?? 'n/a',
        address: this.data.warehouseAddress?.address ?? 'n/a',
        fullAddress: this.data.warehouseAddress?.fullAddress ?? 'n/a',
        ch83CertifiedPerson: this.data.ch83CertifiedPerson?.fullName ?? 'n/a',
        status: this.data.status ?? 'n/a',
      });
      console.log(this.dialogForm.value);
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
