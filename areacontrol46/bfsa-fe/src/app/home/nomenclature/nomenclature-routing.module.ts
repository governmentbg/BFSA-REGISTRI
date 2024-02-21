import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NomenclatureItemComponent } from './components/nomenclature-item/nomenclature-item.component';
import { NomenclatureListComponent } from './components/nomenclature-list/nomenclature-list.component';
import { NomenclatureComponent } from './nomenclature.component';

const routes: Routes = [
  {
    path: '',
    component: NomenclatureComponent,
    children: [
      {
        path: '',
        component: NomenclatureListComponent,
      },
      {
        path: ':code',
        component: NomenclatureItemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class NomenclatureRoutingModule {}
