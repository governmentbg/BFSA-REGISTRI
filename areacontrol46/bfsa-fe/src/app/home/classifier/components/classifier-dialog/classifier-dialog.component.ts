import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ClassifierInterface } from '../../interfaces/classifier-interface';

@Component({
  selector: 'app-classifier-dialog',
  templateUrl: './classifier-dialog.component.html',
  styleUrls: ['./classifier-dialog.component.scss'],
})
export class ClassifierDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    code: '',
    name: ['', Validators.required],
    description: '',
    enabled: true,
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ClassifierInterface>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; classifier: ClassifierInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.classifier) {
      this.dialogForm.patchValue({
        code: this.data.classifier.code,
        name: this.data.classifier.name,
        description: this.data.classifier.description,
        enabled: this.data.classifier.enabled,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
