import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InspectionsListComponent } from './inspections-list/inspections-list.component';
import { InspectionNewComponent } from './inspection-new/inspection-new.component';
import { InspectionDetailsComponent } from './inspection-details/inspection-details.component';

const routes: Routes = [
  { path: '', component: InspectionsListComponent },
  { path: 'new', component: InspectionNewComponent },
  { path: ':id', component: InspectionDetailsComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InspectionsRoutingModule {}
