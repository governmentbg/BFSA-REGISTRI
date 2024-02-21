import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FoodTypeRoutingModule } from './food-type-routing.module';
import { FoodTypeComponent } from './food-type.component';
import { FoodTypeItemComponent } from './components/food-type-item/food-type-item.component';
import { FoodTypeListComponent } from './components/food-type-list/food-type-list.component';
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
import { FoodTypeDialogComponent } from './components/food-type-dialog/food-type-dialog.component';
import { MatDividerModule } from '@angular/material/divider';

@NgModule({
  declarations: [
    FoodTypeComponent,
    FoodTypeItemComponent,
    FoodTypeListComponent,
    FoodTypeDialogComponent,
  ],
  imports: [
    CommonModule,
    FoodTypeRoutingModule,
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
  ],
})
export class FoodTypeModule {}
