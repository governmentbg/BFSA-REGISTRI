import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { BranchInterface } from '../../interfaces/branch-interface';
import { ContractorInterface } from '../../interfaces/contractor-interface';

@Component({
  selector: 'app-contractor-dialog',
  templateUrl: './contractor-dialog.component.html',
  styleUrls: ['./contractor-dialog.component.scss'],
})
export class ContractorDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [''],
    email: ['', [Validators.required, Validators.email]],
    fullName: ['', Validators.required],
    username: [''],
    enabled: true,
    identifier: ['', Validators.required],
    roles: ['', Validators.required],
    branchId: ['', Validators.required],
  });

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      applicant: ContractorInterface;
      roles: string[];
      branches: BranchInterface[];
    }
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    if (!this.data.isAdd) {
      const defaultRoles = this.data.roles?.filter((role: string) =>
        this.data.applicant?.roles?.includes(role)
      );
      const defaultBranch = this.data.branches?.find(
        (branch: BranchInterface) =>
          branch.id === this.data.applicant?.branchId
      );
      this.dialogForm.removeControl('password');
      this.dialogForm.removeControl('matchingPassword');
      this.dialogForm.patchValue({
        id: this.data.applicant?.id,
        email: this.data.applicant?.email,
        fullName: this.data.applicant?.fullName,
        username: this.data.applicant?.username,
        enabled: this.data.applicant?.enabled,
        identifier: this.data.applicant?.identifier,
        roles: defaultRoles,
        branchId: defaultBranch?.id,
      });
    }
    if (this.data.isAdd) {
      this.dialogForm.removeControl('id');
    }
  }

  onSubmit() {
    console.log(this.dialogForm.value);
    this.dialogRef.close(this.dialogForm.value);
  }
}
