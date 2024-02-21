import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import * as saveAs from 'file-saver';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { BranchService } from 'src/app/services/branch.service';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { EditApplicationS503Component } from '../edit-application-s503/edit-application-s503.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import * as dayjs from 'dayjs';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-edit-application-s2869',
  templateUrl: './edit-application-s2869.component.html',
  styleUrls: ['./edit-application-s2869.component.scss'],
})
export class EditApplicationS2869Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public applicationForm: FormGroup;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public activityTypes: NomenclatureInterface[];
  public finalActionErrors: String = '';

  constructor(
    public dialogRef: MatDialogRef<EditApplicationS503Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public refusalDialog: MatDialog,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    private recordService: RecordService,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getAllBranches();
    this.getAllRequestorAuthorTypes();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();
    this.getActivityTypes();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        facilities: this.fb.array([]),
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
      if (this.data?.facilities?.length > 0) {
        this.fillFacilities();
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

  fillFacilities() {
    this.data?.facilities.map((el: any, index: number) => {
      el.activityTypes = el.activityTypes.map((el: any) => el.code);
      const facilityForm = this.fb.group({
        regNumber: el.regNumber,
        activityTypes: [el.activityTypes],
      });
      this.facilities.push(facilityForm);
    });
  }

  get ch50Vehicles() {
    return this.applicationForm?.controls['ch50Vehicles'] as FormArray;
  }

  get ch52Vehicles() {
    return this.applicationForm?.controls['ch52Vehicles'] as FormArray;
  }

  get facilities() {
    return this.applicationForm?.controls['facilities'] as FormArray;
  }

  get carryingOutActivityPersons() {
    return this.applicationForm?.controls[
      'carryingOutActivityPersons'
    ] as FormArray;
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

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
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

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  closeDialog() {
    this.dialogRef.close();
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
              if (res) {
                this.documentService.downloadRefusalDocumentByServiceType(
                  this.data.serviceType.toLowerCase(),
                  this.applicationForm.get('recordId')?.value
                );
              }
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
              const dialogRef = this.dialog
                .open(DataAndDownloadDialogComponent, {
                  height: '300px',
                  width: '400px',
                  data: res,
                })
                .afterClosed()
                .subscribe((res) => {
                  if (res) {
                    this.documentService
                      .downloadDocumentByRecordId(
                        this.applicationForm.get('recordId')?.value
                      )
                      .subscribe((res: any) => {
                        const contentType = res.headers.get('content-type');
                        const fileName = res.headers.get('File-Name');
                        this.downloadFile(res.body, contentType, fileName);
                        this.dialogRef.close(res);
                      });
                  }
                });
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

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
      });
  }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }
}
