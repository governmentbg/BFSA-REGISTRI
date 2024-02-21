import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ActivityGroupInterface } from '../../interfaces/activity-group-interface';

@Component({
  selector: 'app-activity-group-dialog',
  templateUrl: './activity-group-dialog.component.html',
  styleUrls: ['./activity-group-dialog.component.scss'],
})
export class ActivityGroupDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [''],
    name: ['', Validators.required],
    description: ['', Validators.required],
    enabled: [true, Validators.required],
  });

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<ActivityGroupInterface>,
    @Inject(MAT_DIALOG_DATA)
    public data: { isAdd: boolean; activityGroup: ActivityGroupInterface }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (this.data.activityGroup) {
      this.dialogForm.patchValue({
        id: this.data.activityGroup.id,
        name: this.data.activityGroup.name,
        enabled: this.data.activityGroup.enabled,
        description: this.data.activityGroup.description,
      });
    }
  }

  onSubmit() {
    this.dialogRef.close(this.dialogForm.value);
  }
}
