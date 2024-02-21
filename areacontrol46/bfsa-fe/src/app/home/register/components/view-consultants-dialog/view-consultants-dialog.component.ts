import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-view-consultants-dialog',
  templateUrl: './view-consultants-dialog.component.html',
  styleUrls: ['./view-consultants-dialog.component.scss'],
})
export class ViewConsultantsDialogComponent {
  public ch83PersonsForm: FormGroup;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ViewConsultantsDialogComponent>
  ) {}

  ngOnInit() {
    if (this.data) {
      console.log(this.data);
      this.ch83PersonsForm = this.fb.group({
        ch83CertifiedPersons: this.fb.array([]),
      });
      if (this.data?.length > 0) {
        this.fillCh83CertifiedPersons();
      }
    }
    this.ch83PersonsForm.disable();
  }
  fillCh83CertifiedPersons() {
    this.data?.map((el: any, index: number) => {
      const ch83CertifiedPersonForm = this.fb.group({
        identifier: el.identifier,
        fullName: el.fullName,
      });
      this.ch83CertifiedPersons.push(ch83CertifiedPersonForm);
    });
  }

  get ch83CertifiedPersons() {
    return this.ch83PersonsForm?.get('ch83CertifiedPersons') as FormArray;
  }
  closeDialog() {
    this.dialogRef.close();
  }
}
