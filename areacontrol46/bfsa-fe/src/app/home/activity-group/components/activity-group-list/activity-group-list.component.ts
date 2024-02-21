import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { ActivityGroupInterface } from '../../interfaces/activity-group-interface';
import { ActivityGroupService } from '../../services/activity-group.service';
import { ActivityGroupDialogComponent } from '../activity-group-dialog/activity-group-dialog.component';

@Component({
  selector: 'app-activity-group-list',
  templateUrl: './activity-group-list.component.html',
  styleUrls: ['./activity-group-list.component.scss'],
})
export class ActivityGroupListComponent implements OnInit {
  activityGroups: ActivityGroupInterface[]

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private activityGroupService: ActivityGroupService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.activityGroupService
      .getActivityGroups()
      .subscribe((activityGroups: ActivityGroupInterface[]) => {
        this.activityGroups = activityGroups;
      });
  }

  addActivityGroup() {
    const dialogRef = this.dialog.open(ActivityGroupDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef
      .afterClosed()
      .subscribe((activityGroup: ActivityGroupInterface) => {
        if (activityGroup) {
          console.log(activityGroup);
          this.activityGroupService.addActivityGroup(activityGroup).subscribe({
            next: (res) => {
              this.initialize();
              this._snackBar.open('Activity Group added successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while adding the activity group: ${err.message}`,
                'Close'
              );
            },
          });
        }
      });
  }
}
