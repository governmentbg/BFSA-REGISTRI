import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';
import { PageNotFoundComponent } from './home/components/page-not-found/page-not-found.component';
import { NomenclaturesComponent } from './nomenclatures/nomenclatures.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/app/dashboard',
    pathMatch: 'full',
  },
  {
    path: 'nomenclatures',
    component: NomenclaturesComponent,
    canActivate: [AuthGuard],
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then((m) => m.AuthModule),
  },
  {
    path: 'app',
    loadChildren: () => import('./home/home.module').then((m) => m.HomeModule),
    canActivate: [AuthGuard],
  },
  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
