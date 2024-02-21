import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SettlementItemComponent } from './components/settlement-item/settlement-item.component';
import { SettlementListComponent } from './components/settlement-list/settlement-list.component';
import { SettlementComponent } from './settlement.component';

const routes: Routes = [
  {
    path: '',
    component: SettlementComponent,
    children: [
      {
        path: '',
        component: SettlementListComponent,
      },
      {
        path: ':code',
        component: SettlementItemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SettlementRoutingModule {}
