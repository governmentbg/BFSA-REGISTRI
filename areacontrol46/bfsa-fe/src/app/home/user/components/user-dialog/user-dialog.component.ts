import { Component, Inject, OnInit } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BranchInterface } from '../../interfaces/branch-interface';
import { DirectorateCode } from '../../interfaces/directorate-code';
import { UserInterface } from '../../interfaces/user-interface';

// const matchPassword = (
//   firstControl: string,
//   secondControl: string
// ): ValidatorFn => {
//   return (control: AbstractControl): ValidationErrors | null => {
//     const password = control.get(firstControl)?.value;
//     const confirm = control.get(secondControl)?.value;

//     if (password !== confirm) {
//       control.get(secondControl)?.setErrors({ noMatch: true });
//       return { noMatch: true };
//     }

//     return null;
//   };
// };

@Component({
  selector: 'app-user-dialog',
  templateUrl: './user-dialog.component.html',
  styleUrls: ['./user-dialog.component.scss'],
})
export class UserDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [''],
    email: ['', [Validators.required, Validators.email]],
    fullName: ['', Validators.required],
    username: ['', Validators.required],
    enabled: true,
    password: ['', Validators.required],
    //matchingPassword: ['', Validators.required],
    identifier: ['', Validators.required],
    roles: ['', Validators.required],
    branchId: ['', Validators.required],
    directorateCode: [''],
  });

  hide = true;

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<UserDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      user: UserInterface;
      roles: string[];
      branches: BranchInterface[];
      directorateCodeList: DirectorateCode;
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (!this.data.isAdd) {
      this.dialogForm.patchValue({
        id: this.data.user?.id,
        email: this.data.user?.email,
        fullName: this.data.user?.fullName,
        password: this.data.user?.username,
        username: this.data.user?.username,
        enabled: this.data.user?.enabled,
        identifier: this.data.user?.identifier,
        roles: this.data.user?.roles,
        branchId: this.data.user?.branchId,
        directorateCode: this.data.user?.directorateCode,
      });
    }
    if (this.data.isAdd) {
      this.dialogForm.removeControl('id');
    }
  }

  onSubmit() {
    console.log(this.dialogForm.value);
    this.dialogRef.close(this.dialogForm.value);
  }
}
