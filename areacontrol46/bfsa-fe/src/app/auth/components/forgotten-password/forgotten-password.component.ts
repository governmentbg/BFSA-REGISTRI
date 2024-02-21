import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { Meta, Title } from '@angular/platform-browser';
import { UserService } from 'src/app/services/user.service';
import { ApiResponse } from '../../../shared/interfaces/common.interface';

@Component({
  selector: 'app-forgotten-password',
  templateUrl: './forgotten-password.component.html',
  styleUrls: ['./forgotten-password.component.scss'],
})
export class ForgottenPasswordComponent implements OnInit {
  submitted = false;
  successMsg?: string;

  resetPasswordForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  constructor(
    private fb: UntypedFormBuilder,
    private userService: UserService,
    private title: Title,
    private meta: Meta,
    private _snackBar: MatSnackBar
  ) {
    title.setTitle('Restore your password');
  }

  ngOnInit(): void {}

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      return;
    }

    this.userService.resetPassword(this.resetPasswordForm.value).subscribe({
      next: (success: ApiResponse) => {
        this.submitted = true;
        console.log(success.message);
        this.successMsg = success.message;
      },
      error: (e) => {
        console.error(e);
        this._snackBar.open(e.error.error, 'Close', {
          duration: 3000,
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
        });
      },
    });
  }
}
