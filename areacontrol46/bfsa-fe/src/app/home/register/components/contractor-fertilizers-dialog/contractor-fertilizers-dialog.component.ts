import { Component, Inject } from '@angular/core';
import { Validators, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ContractorFoodAdditivesDialogComponent } from '../contractor-food-additives-dialog/contractor-food-additives-dialog.component';
import { applicationStatus } from 'src/app/enums/applicationStatus';

@Component({
  selector: 'app-contractor-fertilizers-dialog',
  templateUrl: './contractor-fertilizers-dialog.component.html',
  styleUrls: ['./contractor-fertilizers-dialog.component.scss'],
})
export class ContractorFertilizersDialogComponent {
  public applicationStatuses = applicationStatus;

  public dialogForm = this.fb.group({
    regNumber: [null, Validators.required],
    regDate: [null, Validators.required],
    name: [null, Validators.required],
    productTypeName: [null, Validators.required],
    manufacturerFullName: [null, Validators.required],
    applicationStatus: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorFoodAdditivesDialogComponent>,
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
        name: this.data.name ?? 'n/a',
        productTypeName: this.data.productTypeName ?? 'n/a',
        manufacturerFullName: this.data.manufacturer?.fullName ?? 'n/a',
        applicationStatus: this.data.applicationStatus ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
