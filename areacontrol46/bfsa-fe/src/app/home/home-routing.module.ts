import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NomenclaturesComponent } from '../nomenclatures/nomenclatures.component';
import { TasksComponent } from './components/tasks/tasks.component';
import { HomeComponent } from './home.component';
import { ApplicationsComponent } from './components/applications/applications.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    children: [
      {
        path: 'tasks',
        component: TasksComponent,
      },
      {
        path: 'applications',
        component: ApplicationsComponent,
      },
      {
        path: 'registers',
        loadChildren: () =>
          import('../home/register/register.module').then(
            (m) => m.RegisterModule
          ),
      },
      // {
      //   path: 'registers/:id',
      //   loadComponent: () =>
      //     import(
      //       './register/components/contractor-details/contractor-details.component'
      //     ).then((c) => c.ContractorDetailsComponent),
      // },
      {
        path: 'dashboard',
        loadChildren: () =>
          import('./dashboard/dashboard.module').then((m) => m.DashboardModule),
      },
      {
        path: 'nomenclature',
        loadChildren: () =>
          import('./nomenclature/nomenclature.module').then(
            (m) => m.NomenclatureModule
          ),
      },
      {
        path: 'settlement',
        loadChildren: () =>
          import('./settlement/settlement.module').then(
            (m) => m.SettlementModule
          ),
      },
      {
        path: 'classifier',
        loadChildren: () =>
          import('./classifier/classifier.module').then(
            (m) => m.ClassifierModule
          ),
      },
      {
        path: 'activity-group',
        loadChildren: () =>
          import('./activity-group/activity-group.module').then(
            (m) => m.ActivityGroupModule
          ),
      },
      {
        path: 'message-resource',
        loadChildren: () =>
          import('./message-resource/message-resource.module').then(
            (m) => m.MessageResourceModule
          ),
      },
      {
        path: 'language',
        loadChildren: () =>
          import('./language/language.module').then((m) => m.LanguageModule),
      },
      {
        path: 'profile',
        loadChildren: () =>
          import('./profile/profile.module').then((m) => m.ProfileModule),
      },
      {
        path: 'user',
        loadChildren: () =>
          import('./user/user.module').then((m) => m.UserModule),
      },
      {
        path: 'applicant',
        loadChildren: () =>
          import('./contractor/contractor.module').then(
            (m) => m.ContractorModule
          ),
      },
      {
        path: 'branch',
        loadChildren: () =>
          import('./branch/branch.module').then((m) => m.BranchModule),
      },
      {
        path: 'inspection',
        loadChildren: () =>
          import('./inspection/inspection.module').then(
            (m) => m.InspectionModule
          ),
      },
      {
        path: 'inspections',
        loadChildren: () =>
          import('./inspections/inspections.module').then(
            (m) => m.InspectionsModule
          ),
      },
      {
        path: 'nomenclatures',
        component: NomenclaturesComponent,
        // canActivate: [AuthGuard],
      },
      { path: '**', redirectTo: 'dashboard', pathMatch: 'full' },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomeRoutingModule {}
