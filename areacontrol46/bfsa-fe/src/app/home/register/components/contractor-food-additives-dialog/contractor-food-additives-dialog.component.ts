import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-contractor-food-additives-dialog',
  templateUrl: './contractor-food-additives-dialog.component.html',
  styleUrls: ['./contractor-food-additives-dialog.component.scss'],
})
export class ContractorFoodAdditivesDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    regNumber: [null, Validators.required],
    type: [null, Validators.required],
    name: [null, Validators.required],
    ingredients: [null, Validators.required],
    purpose: [null, Validators.required],
    releaseDate: [null, Validators.required],
    status: [null, Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorFoodAdditivesDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { item: any; facilityStatusList: FacilityStatus }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data.item) {
      this.dialogForm.patchValue({
        regNumber: this.data.item.regNumber ?? 'n/a',
        type: this.data.item.type ?? 'n/a',
        name: this.data.item.name ?? 'n/a',
        ingredients: this.data.item.ingredients ?? 'n/a',
        purpose: this.data.item.purpose ?? 'n/a',
        releaseDate: this.data.item.releaseDate ?? 'n/a',
        status: this.data.item.status ?? 'n/a',
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
