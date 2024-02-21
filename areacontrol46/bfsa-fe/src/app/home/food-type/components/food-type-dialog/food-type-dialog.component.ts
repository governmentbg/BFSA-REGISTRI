import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FoodTypeInterface } from '../../interfaces/food-type-interface';

@Component({
  selector: 'app-food-type-dialog',
  templateUrl: './food-type-dialog.component.html',
  styleUrls: ['./food-type-dialog.component.scss'],
})
export class FoodTypeDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    code: '',
    name: ['', Validators.required],
    description: '',
    enabled: true,
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<FoodTypeInterface>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; foodType: FoodTypeInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.foodType) {
      this.dialogForm.patchValue({
        code: this.data.foodType.code,
        name: this.data.foodType.name,
        description: this.data.foodType.description,
        enabled: this.data.foodType.enabled,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
