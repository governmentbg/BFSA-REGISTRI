import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MessageResourceRoutingModule } from './message-resource-routing.module';
import { MessageResourceComponent } from './message-resource.component';
import { ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { MessageResourceListComponent } from './components/message-resource-list/message-resource-list.component';
import { MessageResourceDialogComponent } from './components/message-resource-dialog/message-resource-dialog.component';
import {TranslateModule} from '@ngx-translate/core';

@NgModule({
  declarations: [
    MessageResourceComponent,
    MessageResourceListComponent,
    MessageResourceDialogComponent,
  ],
  imports: [
    CommonModule,
    MessageResourceRoutingModule,
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
    TranslateModule,
  ],
})
export class MessageResourceModule {}
