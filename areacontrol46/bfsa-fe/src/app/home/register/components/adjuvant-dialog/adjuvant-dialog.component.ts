import { Component, Inject } from '@angular/core';
import { Validators, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';
import { ContractorCertificates83DialogComponent } from '../contractor-certificates83-dialog/contractor-certificates83-dialog.component';
import { applicationStatus } from 'src/app/enums/applicationStatus';

@Component({
  selector: 'app-adjuvant-dialog',
  templateUrl: './adjuvant-dialog.component.html',
  styleUrls: ['./adjuvant-dialog.component.scss'],
})
export class AdjuvantDialogComponent {
  public applicationStatuses = Object.entries(applicationStatus);
  public dialogForm = this.fb.group({
    adjuvantName: [null, Validators.required],
    adjuvantProductFormulationTypeName: [null, Validators.required],
    manufacturerName: [null, Validators.required],
    supplierFullName: [null, Validators.required],
    orderNumber: [null, Validators.required],
    applicationStatus: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorCertificates83DialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: any
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data) {
      this.dialogForm.patchValue({
        adjuvantName: this.data.adjuvantName ?? 'n/a',
        adjuvantProductFormulationTypeName:
          this.data.adjuvantProductFormulationTypeName ?? 'n/a',
        manufacturerName: this.data.manufacturerName ?? 'n/a',
        supplierFullName: this.data.supplier?.fullName ?? 'n/a',
        orderNumber: this.data.orderNumber ?? 'n/a',
        applicationStatus: this.data.applicationStatus ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(applicationStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }
}
