import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SettlementRoutingModule } from './settlement-routing.module';
import { SettlementComponent } from './settlement.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatInputModule } from '@angular/material/input';
import { SettlementListComponent } from './components/settlement-list/settlement-list.component';
import { SettlementItemComponent } from './components/settlement-item/settlement-item.component';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { SettlementDialogComponent } from './components/settlement-dialog/settlement-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [
    SettlementComponent,
    SettlementListComponent,
    SettlementItemComponent,
    SettlementDialogComponent,
  ],
  imports: [
    CommonModule,
    SettlementRoutingModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    MatInputModule,
    MatIconModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    MatDividerModule,
    TranslateModule,
  ],
})
export class SettlementModule {}
