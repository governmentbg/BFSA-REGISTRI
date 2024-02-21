import { Component, Inject } from '@angular/core';
import { EditApplicationS3181Component } from '../edit-application-s3181/edit-application-s3181.component';
import { DatePipe } from '@angular/common';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import * as dayjs from 'dayjs';
import * as saveAs from 'file-saver';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
@Component({
  selector: 'app-edit-application-s3362',
  templateUrl: './edit-application-s3362.component.html',
  styleUrls: ['./edit-application-s3362.component.scss'],
})
export class EditApplicationS3362Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public measuringUnits: NomenclatureInterface[];
  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS3181Component>,
    public refusalDialog: MatDialog,
    private recordService: RecordService,
    private fb: UntypedFormBuilder,
  ) {}

  ngOnInit() {
    if (this.data) {
      this.buildForm();
      this.applicationForm.disable();
    }
  }

  buildForm() {
    this.applicationForm = this.fb.group({
      recordId: [this.data?.recordId],
      fullName: [this.data?.fullName],
      registrationDate: [this.formatDate(this.data?.registrationDate)],
      contractStartDate: [this.formatDate(this.data?.contractStartDate)],
      contractEndDate: [this.formatDate(this.data?.contractEndDate)],
      pppName: [this.data?.pppName],
      pppManufacturerName: [this.data?.pppManufacturerName],
      pppPackageVolume: [this.data?.pppPackageVolume],
      pppPackageUnitCode: [this.data?.pppPackageUnitCode],
      pppPackageUnitName: [this.data?.pppPackageUnitName],
      pppPackageMaterial: [this.data?.pppPackageMaterial],
      packageVolume: [this.data?.packageVolume],
      packageUnitCode: [this.data?.packageUnitCode],
      packageUnitName: [this.data?.packageUnitName],
      packageMaterial: [this.data?.packageMaterial],
    });
  }

  get isApplicationStatusPaymentConfirmedOrEntered() {
    if (
      this.data?.recordStatus === 'PAYMENT_CONFIRMED' ||
      this.data?.recordStatus === 'ENTERED' ||
      this.data?.recordStatus === 'INSPECTION_COMPLETED'
    ) {
      return true;
    }
    return false;
  }

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  openCorrectionDialog() {
    const dialogRef = this.dialog.open(ForCorrectionDialogComponent, {
      height: '400px',
      width: '550px',
    });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const body = {
          id: this.applicationForm.get('recordId')?.value,
          description: result,
        };
        this.recordService
          .forCorrectionRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            body
          )
          .subscribe({
            next: (res) => {
              console.log(res);
            },
            error: (err) => {
              if (err) {
                console.log(err);
                this.finalActionErrors += err.error;
              }
            },
          });
      }
    });
  }

  public openRefusalDialog() {
    const dialogRef = this.dialog.open(RefusalDialogComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.recordService
          .refuseRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value
          )
          .subscribe({
            next: (res) => {
              console.log(res);
              this.dialogRef.close(res);
            },
            error: (err) => {
              if (err) {
                console.log(err);
                this.finalActionErrors += err.error;
              }
            },
          });
      }
    });
  }

  public openApprovalDialog() {
    const dialogRef = this.dialog.open(ApprovalDialogComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.recordService
          .approveRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value
          )
          .subscribe({
            next: (res) => {
              console.log(res);
            },
            error: (err) => {
              if (err) {
                console.log(err);
                this.finalActionErrors += err.error.message;
              }
            },
          });
      }
    });
  }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }
}
