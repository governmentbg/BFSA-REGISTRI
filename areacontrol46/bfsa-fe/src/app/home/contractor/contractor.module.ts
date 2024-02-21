import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ContractorRoutingModule } from './contractor-routing.module';
import { ContractorComponent } from './contractor.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ContractorListComponent } from './components/contractor-list/contractor-list.component';
import { ContractorDialogComponent } from './components/contractor-dialog/contractor-dialog.component';

@NgModule({
  declarations: [ContractorComponent, ContractorListComponent, ContractorDialogComponent],
  imports: [
    CommonModule,
    ContractorRoutingModule,
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
    MatSlideToggleModule,
    MatSelectModule,
  ],
})
export class ContractorModule {}
