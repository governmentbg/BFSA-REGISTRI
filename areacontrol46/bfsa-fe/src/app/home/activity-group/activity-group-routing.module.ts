import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ActivityGroupComponent } from './activity-group.component';
import { ActivityGroupItemComponent } from './components/activity-group-item/activity-group-item.component';
import { ActivityGroupListComponent } from './components/activity-group-list/activity-group-list.component';

const routes: Routes = [
  {
    path: '',
    component: ActivityGroupComponent,
    children: [
      {
        path: '',
        component: ActivityGroupListComponent,
      },
      {
        path: ':id',
        component: ActivityGroupItemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActivityGroupRoutingModule {}
