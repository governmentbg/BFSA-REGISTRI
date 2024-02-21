import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthComponent } from './auth.component';
import { EntranceTypeComponent } from './components/entrance-type/entrance-type.component';
import { ForgottenPasswordComponent } from './components/forgotten-password/forgotten-password.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { SigninComponent } from './components/signin/signin.component';
import { VerifyPasswordComponent } from './components/verify-password/verify-password.component';
import { VerifyRegistrationComponent } from './components/verify-registration/verify-registration.component';

const routes: Routes = [
  {
    path: '',
    component: AuthComponent,
    children: [
      // {
      //   path: 'entranceType',
      //   component: EntranceTypeComponent,
      // },
      {
        path: 'login',
        component: LoginComponent,
      },
      // {
      //   path: 'signin',
      //   component: SigninComponent,
      // },
      { path: 'register', component: RegisterComponent },
      { path: 'user-registration', component: VerifyRegistrationComponent },
      { path: 'forgotten-password', component: ForgottenPasswordComponent },
      { path: 'forgot-password', component: VerifyPasswordComponent },
       { path: '**', redirectTo: '/auth/login', pathMatch: 'full' },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AuthRoutingModule {}
