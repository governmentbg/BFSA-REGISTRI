import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MessageResourceComponent } from './message-resource.component';

const routes: Routes = [{ path: '', component: MessageResourceComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MessageResourceRoutingModule { }
