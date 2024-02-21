import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ContractorDetailsComponent } from './components/contractor-details/contractor-details.component';
import { RegisterPageComponent } from './register-page/register-page.component';

const routes: Routes = [
  { path: '', component: RegisterPageComponent },
  { path: ':id', component: ContractorDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RegisterRoutingModule {}
