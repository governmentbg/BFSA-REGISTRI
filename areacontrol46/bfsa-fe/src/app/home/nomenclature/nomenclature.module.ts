import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NomenclatureRoutingModule } from './nomenclature-routing.module';
import { NomenclatureComponent } from './nomenclature.component';
import { NomenclatureDialogComponent } from './components/nomenclature-dialog/nomenclature-dialog.component';
import { NomenclatureItemComponent } from './components/nomenclature-item/nomenclature-item.component';
import { NomenclatureListComponent } from './components/nomenclature-list/nomenclature-list.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [
    NomenclatureComponent,
    NomenclatureDialogComponent,
    NomenclatureItemComponent,
    NomenclatureListComponent,
  ],
  imports: [
    CommonModule,
    NomenclatureRoutingModule,
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
export class NomenclatureModule {}
