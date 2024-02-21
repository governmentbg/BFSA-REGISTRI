import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { Meta, Title } from '@angular/platform-browser';
import { UserService } from 'src/app/services/user.service';
import { ApiResponse } from '../../../shared/interfaces/common.interface';

const matchPassword = (
  firstControl: string,
  secondControl: string
): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get(firstControl)?.value;
    const confirm = control.get(secondControl)?.value;

    if (password !== confirm) {
      control.get(secondControl)?.setErrors({ noMatch: true });
      return { noMatch: true };
    }

    return null;
  };
};

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
})
export class RegisterComponent implements OnInit {
  hide = true;
  hideMatchingPassword = true;
  submitted = false;
  successMsg?: string;

  registerForm = this.fb.group(
    {
      email: [null, [Validators.required, Validators.email]],
      username: [null, [Validators.required]],
      fullName: [null, [Validators.required]],
      identifier: [null, Validators.required],
      password: [null, [Validators.required, Validators.minLength(6)]],
      matchingPassword: [null, Validators.required],
    },
    {
      validators: [matchPassword('password', 'matchingPassword')],
    }
  );

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    private fb: UntypedFormBuilder,
    private userService: UserService,
    private title: Title,
    private meta: Meta,
    private _snackBar: MatSnackBar,
  ) {
    title.setTitle('Register');
  }

  ngOnInit(): void {
  }



  onSubmit() {
    if (this.registerForm.invalid) {
      return;
    }

    this.userService.register(this.registerForm.value).subscribe({
      next: (success: ApiResponse) => {
        this.submitted = true;
        this.successMsg = success.message;
      },
      error: (e) => {
        console.error(e);
        this._snackBar.open(e.error.message, 'Close', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      },
    });
  }
}
