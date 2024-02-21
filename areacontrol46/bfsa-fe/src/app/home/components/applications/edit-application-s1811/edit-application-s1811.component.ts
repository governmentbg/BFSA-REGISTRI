import { Component, Inject, ViewChild } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { EditApplicationS1199Component } from '../edit-application-s1199/edit-application-s1199.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import * as dayjs from 'dayjs';

@Component({
  selector: 'app-edit-application-s1811',
  templateUrl: './edit-application-s1811.component.html',
  styleUrls: ['./edit-application-s1811.component.scss'],
})
export class EditApplicationS1811Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public applicationForm: FormGroup;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public finalActionErrors: String = '';

  constructor(
    public dialogRef: MatDialogRef<EditApplicationS1199Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public refusalDialog: MatDialog,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    private recordService: RecordService
  ) {}

  get ch50Vehicles() {
    return this.applicationForm?.controls['ch50Vehicles'] as FormArray;
  }

  get ch52Vehicles() {
    return this.applicationForm?.controls['ch52Vehicles'] as FormArray;
  }

  ngOnInit() {
    this.getAllBranches();
    this.getAllRequestorAuthorTypes();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        facilityRegNumber: [this.data?.facilityRegNumber],
        ch50Vehicles: this.fb.array([]),
        ch52Vehicles: this.fb.array([]),
        commencementActivityDate: [
          this.formatDate(this.data.commencementActivityDate),
        ],
      });
      if (this.data?.ch50Vehicles?.length > 0) {
        this.fillCh50Vehicles();
      }
      if (this.data?.ch52Vehicles?.length > 0) {
        this.fillCh52Vehicles();
      }
      this.applicationForm.disable();
    }
  }

  fillCh50Vehicles() {
    this.data?.ch50Vehicles.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        certificateNumber: el.certificateNumber,
      });
      this.ch50Vehicles.push(vehicleForm);
    });
  }

  fillCh52Vehicles() {
    this.data?.ch52Vehicles.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        vehicleArt52Description: el.vehicleArt52Description,
        vehicleOwnershipTypeCode: el.vehicleOwnershipTypeCode,
        vehicleTypeCode: el.vehicleTypeCode,
        brandModel: el.brandModel,
        registrationPlate: el.registrationPlate,
        vehicleArt50: el.vehicleArt50,
        certificateNumber: el.certificateNumber,
      });
      this.ch52Vehicles.push(vehicleForm);
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

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  private getAllVehicleOwnerships() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01600')
      .subscribe((res) => {
        this.vehicleOwnerships = res;
      });
  }

  private getAllVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res) => {
        this.vehicleTypes = res;
      });
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
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
}
