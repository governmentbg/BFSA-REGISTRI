import { Component, Inject, Input } from '@angular/core';
import { Validators, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorPlantProtectionServicesDialogComponent } from '../contractor-plant-protection-services-dialog/contractor-plant-protection-services-dialog.component';
import { recordStatus } from 'src/app/enums/recordStatus';
import * as dayjs from 'dayjs';

@Component({
  selector: 'app-permissions-dialog',
  templateUrl: './permissions-dialog.component.html',
  styleUrls: ['./permissions-dialog.component.scss'],
})
export class PermissionsDialogComponent {
  @Input('options') options: any[] = [];
  @Input('applicant') applicant: ContractorInterface;

  dialogForm = this.fb.group({
    approvalDocumentNumber: [null, Validators.required],
    firstEntryDate: [null, Validators.required],
    materialType: [null, Validators.required],
    materialTotalAmount: [null, Validators.required],
    recordStatus: [null, Validators.required],
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
    console.log(this.data);

    if (this.data.item) {
      this.dialogForm.patchValue({
        approvalDocumentNumber: this.data.item.approvalDocumentNumber ?? 'n/a',
        firstEntryDate: this.formatDate(this.data.item.firstEntryDate) ?? 'n/a',
        materialType: this.data.item.materialType ?? 'n/a',
        materialTotalAmount: this.data.item.materialTotalAmount ?? 'n/a',
        recordStatus:
          this.translateStatus(this.data.item.recordStatus) ?? 'n/a',
      });
    }
  }

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(recordStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      console.log(statuses);
      return statuses[index][1];
    }
    return status;
  }

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
