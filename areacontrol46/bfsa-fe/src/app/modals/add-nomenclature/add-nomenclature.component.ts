import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-add-nomenclature',
  templateUrl: './add-nomenclature.component.html',
  styleUrls: ['./add-nomenclature.component.scss'],
})
export class AddNomenclatureComponent implements OnInit {
  public nomenclatureForm: UntypedFormGroup;

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<AddNomenclatureComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit(): void {
    if (!this.data) {
      this.nomenclatureForm = this.fb.group({
        name: [''],
        description: [''],
        enabled: true,
      });
    } else {
      this.nomenclatureForm = this.fb.group({
        name: [this.data.name],
        description: [this.data.description],
        enabled: true,
      });
    }
  }

  randomMethod() {}

  saveNomenclatureCategory() {
    this.dialogRef.close({
      nomenclature: this.nomenclatureForm.value,
    });
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
}
