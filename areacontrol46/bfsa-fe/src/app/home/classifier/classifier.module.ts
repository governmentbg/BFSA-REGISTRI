import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ClassifierRoutingModule } from './classifier-routing.module';
import { ClassifierComponent } from './classifier.component';
import { ClassifierDialogComponent } from './components/classifier-dialog/classifier-dialog.component';
import { ClassifierItemComponent } from './components/classifier-item/classifier-item.component';
import { ClassifierListComponent } from './components/classifier-list/classifier-list.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [
    ClassifierComponent,
    ClassifierDialogComponent,
    ClassifierItemComponent,
    ClassifierListComponent,
  ],
  imports: [
    CommonModule,
    ClassifierRoutingModule,
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
export class ClassifierModule {}
