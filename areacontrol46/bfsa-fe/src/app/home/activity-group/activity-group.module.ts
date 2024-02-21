import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ActivityGroupRoutingModule } from './activity-group-routing.module';
import { ActivityGroupComponent } from './activity-group.component';
import { ActivityGroupDialogComponent } from './components/activity-group-dialog/activity-group-dialog.component';
import { ActivityGroupItemComponent } from './components/activity-group-item/activity-group-item.component';
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
import { MatSelectModule } from '@angular/material/select';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { ActivityGroupListComponent } from './components/activity-group-list/activity-group-list.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { TranslateModule } from '@ngx-translate/core';

@NgModule({
  declarations: [
    ActivityGroupComponent,
    ActivityGroupDialogComponent,
    ActivityGroupItemComponent,
    ActivityGroupListComponent,
  ],
  imports: [
    CommonModule,
    ActivityGroupRoutingModule,
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
    MatSelectModule,
    MatDividerModule,
    MatListModule,
    MatCheckboxModule,
    TranslateModule,
  ],
})
export class ActivityGroupModule {}
