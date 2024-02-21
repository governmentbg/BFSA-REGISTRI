import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { InspectionsListComponent } from './inspections-list/inspections-list.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { InspectionNewComponent } from './inspection-new/inspection-new.component';
import { InspectionDetailsComponent } from './inspection-details/inspection-details.component';
import { InspectionConfirmDialogComponent } from './inspection-confirm-dialog/inspection-confirm-dialog.component';
import { InspectionsComponent } from './inspections.component';
import { InspectionsRoutingModule } from './inspections-routing.module';

@NgModule({
  declarations: [
    InspectionsComponent,
    InspectionsListComponent,
    InspectionNewComponent,
    InspectionDetailsComponent,
    InspectionConfirmDialogComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    TranslateModule,
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatDividerModule,
    MatDatepickerModule,
    MatSelectModule,
    MatOptionModule,
    InspectionsRoutingModule,
  ],
})
export class InspectionsModule {}
