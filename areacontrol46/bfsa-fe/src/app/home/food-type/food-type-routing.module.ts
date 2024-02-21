import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FoodTypeItemComponent } from './components/food-type-item/food-type-item.component';
import { FoodTypeListComponent } from './components/food-type-list/food-type-list.component';
import { FoodTypeComponent } from './food-type.component';

const routes: Routes = [
  {
    path: '',
    component: FoodTypeComponent,
    children: [
      {
        path: '',
        component: FoodTypeListComponent,
      },
      {
        path: ':id',
        component: FoodTypeItemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FoodTypeRoutingModule {}
