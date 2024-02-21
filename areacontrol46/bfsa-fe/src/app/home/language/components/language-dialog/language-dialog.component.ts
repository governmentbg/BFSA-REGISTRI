import { Component, Inject, OnInit } from '@angular/core';
import { Validators, FormBuilder, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LanguageInterface } from '../../interfaces/language-interface';

@Component({
  selector: 'app-language-dialog',
  templateUrl: './language-dialog.component.html',
  styleUrls: ['./language-dialog.component.scss'],
})
export class LanguageDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    languageId: ['', [Validators.required]],
    name: ['', [Validators.required]],
    locale: ['', [Validators.required]],
    description: ['', [Validators.required]],
    main: [null],
    enabled: [true],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<LanguageDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; language: LanguageInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.language) {
      this.dialogForm.patchValue({
        languageId: this.data.language.languageId,
        name: this.data.language.name,
        locale: this.data.language.locale,
        description: this.data.language.description,
        main: this.data.language.main,
        enabled: this.data.language.enabled,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
