import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-contractor-certificates83-dialog',
  templateUrl: './contractor-certificates83-dialog.component.html',
  styleUrls: ['./contractor-certificates83-dialog.component.scss'],
})
export class ContractorCertificates83DialogComponent implements OnInit {
  dialogForm = this.fb.group({
    certificateNumber: [null, Validators.required],
    validity: [null, Validators.required],
    type: [null, Validators.required],
    docNumber: [null, Validators.required],
    dataOfIssue: [null, Validators.required],
    status: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorCertificates83DialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { item: any; facilityStatusList: FacilityStatus }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data.item) {
      this.dialogForm.patchValue({
        certificateNumber: this.data.item.certificateNumber ?? 'n/a',
        validity: this.data.item.validity ?? 'n/a',
        docNumber: this.data.item.docNumber ?? 'n/a',
        dataOfIssue: this.data.item.dataOfIssue ?? 'n/a',
        status: this.data.item.status ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
