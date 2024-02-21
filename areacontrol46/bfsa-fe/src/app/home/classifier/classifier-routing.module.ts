import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ClassifierComponent } from './classifier.component';
import { ClassifierItemComponent } from './components/classifier-item/classifier-item.component';
import { ClassifierListComponent } from './components/classifier-list/classifier-list.component';

const routes: Routes = [
  {
    path: '',
    component: ClassifierComponent,
    children: [
      {
        path: '',
        component: ClassifierListComponent,
      },
      {
        path: ':id',
        component: ClassifierItemComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ClassifierRoutingModule {}
