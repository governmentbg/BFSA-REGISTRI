import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { EditApplicationS3181Component } from '../edit-application-s3181/edit-application-s3181.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { DatePipe } from '@angular/common';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-edit-application-s7694',
  templateUrl: './edit-application-s7694.component.html',
  styleUrls: ['./edit-application-s7694.component.scss'],
})
export class EditApplicationS7694Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public requestorAuthorTypes: NomenclatureInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public finallySelectedFoodTypes: { code: string }[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS3181Component>,
    public refusalDialog: MatDialog,
    private recordService: RecordService,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private documentService: DocumentService,
    private datePipe: DatePipe
  ) {}

  get vehicles() {
    return this.applicationForm.controls['vehicles'] as FormArray;
  }

  get ch50VehicleCertNumbers() {
    return this.applicationForm.controls['ch50VehicleCertNumbers'] as FormArray;
  }

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    if (this.data.foodTypes) {
      this.data.foodTypes?.map((foodType: FoodTypeInterface) => {
        // fill new array to break reference from dataSource
        this.finallySelectedFoodTypes.push({ code: foodType.code });
      });
    }
    if (this.data) {
      this.buildForm();
    }
  }

  buildForm() {
    this.data.commencementActivityDate = this.datePipe.transform(
      this.data.commencementActivityDate,
      'dd-MM-yyyy'
    );

    this.applicationForm = this.fb.group({
      recordId: [this.data.recordId],
      name: [this.data.name],
      foodTypeDescription: [this.data.foodTypeDescription],
      vehicles: this.fb.array([]),
      foodTypes: [this.data.foodTypes],
      commencementActivityDate: [this.data.commencementActivityDate],
      ch50VehicleCertNumbers: this.fb.array([]),
    });

    this.applicationForm.disable();

    if (this.data.vehicles?.length) {
      this.fillVehicles();
    }
  }

  fillVehicles() {
    this.data.vehicles.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        vehicleOwnershipTypeName: [
          { value: el.vehicleOwnershipTypeName, disabled: true },
        ],
        vehicleTypeName: [{ value: el.vehicleTypeName, disabled: true }],
        registrationPlate: [{ value: el.registrationPlate, disabled: true }],
        brandModel: [{ value: el.brandModel, disabled: true }],
        certificateNumber: [{ value: el.certificateNumber, disabled: true }],
      });
      if (!el.certificateNumber) {
        this.vehicles.push(vehicleForm);
      } else {
        this.ch50VehicleCertNumbers.push(vehicleForm);
      }
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

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
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
            this.applicationForm.get('recordId')?.value,
            this.finallySelectedFoodTypes
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

  closeDialog() {
    this.dialogRef.close();
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  downloadOrder() {
    this.documentService
      .downloadOrderByRecordId(this.applicationForm.get('recordId')?.value)
      .subscribe((res: any) => {
        const contentType = res.headers.get('content-type');
        const fileName = res.headers.get('File-Name');
        this.downloadFile(res.body, contentType, fileName);
      });
  }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }

  onToggleChange(data: { foodType: any; checked: boolean }) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    if (!isChecked) {
      this.finallySelectedFoodTypes.map(
        (el: { code: string }, index: number) => {
          if (el.code === code) {
            this.finallySelectedFoodTypes.splice(index, 1);
          }
          return el;
        }
      );
    } else {
      this.finallySelectedFoodTypes.push({ code: foodType.code });
    }
  }
}
