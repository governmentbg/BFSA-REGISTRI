import { Component, Inject, ViewChild } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import * as dayjs from 'dayjs';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { CountryService } from 'src/app/home/inspection/services/country.service';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { EditApplicationS1199Component } from '../edit-application-s1199/edit-application-s1199.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';

@Component({
  selector: 'app-edit-application-s3125',
  templateUrl: './edit-application-s3125.component.html',
  styleUrls: ['./edit-application-s3125.component.scss'],
})
export class EditApplicationS3125Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public applicationForm: FormGroup;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public operatorTypes: NomenclatureInterface[];
  public foodSupplementTypes: NomenclatureInterface[];
  public measurementUnits: NomenclatureInterface[];
  public euCountries: any[];
  public finalActionErrors: String = '';

  constructor(
    public dialogRef: MatDialogRef<EditApplicationS1199Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public refusalDialog: MatDialog,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    private recordService: RecordService,
    private readonly countryService: CountryService,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getBranches();
    this.getRequestorAuthorTypes();
    this.getVehicleTypes();
    this.getVehicleOwnerships();
    this.getMeasuringUnits();
    this.getEuropeanCountries();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        applicantTypeName: [this.data.applicantTypeName],
        facilityType: [this.data.facilityType],
        facilityAddress: [this.data.facilityAddress],
        foodSupplements: this.fb.array([]),
        remoteTrade: [this.data.remoteTrade ? this.data.remoteTrade : false],
        //remoteAddress
        distanceTradingAddress: this.fb.group({
          phone: [this.data.distanceTradingAddress?.phone],
          mail: [this.data.distanceTradingAddress?.mail],
          url: [this.data.distanceTradingAddress?.url],
          fullAddress: [this.data.distanceTradingAddress?.fullAddress],
        }),

        address: [this.data.address],
        commencementActivityDate: [
          this.formatDate(this.data.commencementActivityDate),
        ],
      });
      if (this.data?.foodSupplements?.length) {
        this.fillFoodSupplements();
      }
    }
    this.applicationForm.disable();
  }

  fillFoodSupplements() {
    this.data?.foodSupplements?.map((el: any, index: number) => {
      const foodSupplementForm = this.fb.group({
        foodSupplementTypeName: el.foodSupplementTypeName,
        name: el.name,
        purpose: el.purpose,
        ingredients: el.ingredients,
        addressTypeCode: el.addressTypeCode,
        quantity: el.quantity,
        measuringUnitName: el.measuringUnitName,
        markedAnotherEUState: el.markedAnotherEUState,
        countries: [el.countries.map((el: any) => el.code)],
        distributionFacilityAddress: el.distributionFacilityAddress,
        manufactureFacilityAddress: el.manufactureFacilityAddress,
        facilityTypeName: el.facilityTypeName,
      });
      this.foodSupplements.push(foodSupplementForm);
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

  private getRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  private getVehicleOwnerships() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01600')
      .subscribe((res) => {
        this.vehicleOwnerships = res;
      });
  }

  private getVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res) => {
        this.vehicleTypes = res;
      });
  }

  private getEuropeanCountries() {
    this.countryService.getEuropeanCountries().subscribe((res: any) => {
      this.euCountries = res;
    });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measurementUnits = response;
      });
  }

  private getBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  closeDialog() {
    this.dialogRef.close();
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

  get isOperatorTypeOne() {
    return this.applicationForm.get('operatorType')?.value === '02201';
  }

  get foodSupplements() {
    return this.applicationForm?.get('foodSupplements') as FormArray;
  }
}
