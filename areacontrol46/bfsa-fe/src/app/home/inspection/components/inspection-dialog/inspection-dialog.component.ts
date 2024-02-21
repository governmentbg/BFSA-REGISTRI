import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { RecordService } from 'src/app/services/record.service';
import * as dayjs from 'dayjs';
import { DateAdapter } from '@angular/material/core';
import { UserInterface } from 'src/app/home/user/interfaces/user-interface';
import { UserService } from 'src/app/home/user/services/user.service';

@Component({
  selector: 'app-inspection-dialog',
  templateUrl: './inspection-dialog.component.html',
  styleUrls: ['./inspection-dialog.component.scss'],
})
export class InspectionDialogComponent implements OnInit {
  public inspectionForm = this.fb.group({
    recordId: [this.data?.recordId],
    users: [null, Validators.required],
    endDate: [dayjs(new Date()).format('YYYY-MM-DD'), Validators.required],
    description: [null, Validators.required],
  });

  users: UserInterface[];
  minDate = new Date();
  filterDate = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    // Prevent Saturday and Sunday from being selected.
    return day !== 0 && day !== 6;
  };

  constructor(
    private fb: UntypedFormBuilder,
    private readonly userService: UserService,
    private readonly recordService: RecordService,
    public dialogRef: MatDialogRef<InspectionDialogComponent>,
    private dateAdapter: DateAdapter<Date>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    dialogRef.disableClose = true;
    this.dateAdapter.setLocale('bg-BG');
  }

  ngOnInit() {
    this.getAllExperts();
  }

  getAllExperts() {
    this.userService.getUsers().subscribe((users: UserInterface[]) => {
      this.users = users;
    });
  }

  onSubmit() {
    this.inspectionForm.controls['endDate'].setValue(
      dayjs(this.inspectionForm.controls['endDate'].value).format('YYYY-MM-DD')
    );
    console.log(this.inspectionForm.value);
    this.recordService
      .createInspection(this.inspectionForm.value)
      .subscribe(() => {
        this.dialogRef.close(true);
      });
  }
}
