import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuthRoutingModule } from './auth-routing.module';
import { AuthComponent } from './auth.component';
import { LoginComponent } from './components/login/login.component';
import { ForgottenPasswordComponent } from './components/forgotten-password/forgotten-password.component';
import { VerifyPasswordComponent } from './components/verify-password/verify-password.component';
import { RegisterComponent } from './components/register/register.component';
import { VerifyRegistrationComponent } from './components/verify-registration/verify-registration.component';

import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { MatCardModule } from '@angular/material/card';
import { ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TranslateModule } from '@ngx-translate/core';
import { LogoComponent } from './components/logo/logo.component';
import { EntranceTypeComponent } from './components/entrance-type/entrance-type.component';
import { SigninComponent } from './components/signin/signin.component';

@NgModule({
  declarations: [
    AuthComponent,
    LoginComponent,
    ForgottenPasswordComponent,
    VerifyPasswordComponent,
    RegisterComponent,
    VerifyRegistrationComponent,
    LogoComponent,
    EntranceTypeComponent,
    SigninComponent
  ],
  imports: [
    CommonModule,
    AuthRoutingModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatRadioModule,
    MatCardModule,
    ReactiveFormsModule,
    MatIconModule,
    MatCheckboxModule,
    TranslateModule,
  ],
})
export class AuthModule {}
