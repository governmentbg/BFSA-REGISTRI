import { DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import * as saveAs from 'file-saver';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { EditApplicationS3181Component } from '../edit-application-s3181/edit-application-s3181.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-edit-application-s7696',
  templateUrl: './edit-application-s7696.component.html',
  styleUrls: ['./edit-application-s7696.component.scss'],
})
export class EditApplicationS7696Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public requestorAuthorTypes: NomenclatureInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public foodTypes: FoodTypeInterface[];
  public finallySelectedFoodTypes = new Map();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS3181Component>,
    public refusalDialog: MatDialog,
    private recordService: RecordService,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private datePipe: DatePipe,
    private documentService: DocumentService
  ) {}

  get vehicles() {
    return this.applicationForm.controls['vehicles'] as FormArray;
  }

  get ch50VehicleCertNumbers() {
    return this.applicationForm.controls['ch50VehicleCertNumbers'] as FormArray;
  }

  get facilities() {
    return this.applicationForm.controls['facilities'] as FormArray;
  }

  getFacilityFoodTypes(index: number) {
    const facilityFormGroup = (
      this.applicationForm.get('facilities') as FormArray
    ).at(index) as FormGroup;
    return facilityFormGroup.get('foodTypes')?.value;
  }

  ngOnInit() {
    this.getWaterSupplyTypes();
    if (this.data) {
      this.buildForm();
      this.applicationForm.disable();
    }
  }

  buildForm() {
    this.data.commencementActivityDate = this.datePipe.transform(
      this.data.commencementActivityDate,
      'dd-MM-yyyy'
    );

    this.applicationForm = this.fb.group({
      recordId: [this.data.recordId],
      facilities: this.fb.array([]),
      vehicles: this.fb.array([]),
      ch50VehicleCertNumbers: this.fb.array([]),
      commencementActivityDate: [this.data.commencementActivityDate],
    });
    this.applicationForm.disable();

    if (this.data.vehicles?.length) {
      this.fillVehicles();
    }

    if (this.data.facilities?.length) {
      this.fillFacilities();
      // console.log(this.finallySelectedFoodTypes);
    }
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

  fillFacilities() {
    this.data.facilities.map((el: any, index: number) => {
      this.finallySelectedFoodTypes.set(index, new Set(el.foodTypes));

      const facilityForm = this.fb.group({
        facilityTypeName: [{ value: el.facilityTypeName, disabled: true }],
        name: [{ value: el.name, disabled: true }],
        area: [{ value: el.area, disabled: true }],
        fullAddress: [{ value: el.address?.fullAddress, disabled: true }],
        activityDescription: [
          { value: el.activityDescription, disabled: true },
        ],
        waterSupplyTypeName: [
          { value: el.waterSupplyTypeName, disabled: true },
        ],
        disposalWasteWater: [{ value: el.disposalWasteWater, disabled: true }],
        foodTypeDescription: [
          { value: el.foodTypeDescription, disabled: true },
        ],
        foodTypes: [{ value: el.foodTypes, disabled: true }],
        identifier: [el.identifier],
      });
      this.facilities.push(facilityForm);
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
        const facilityFoodTypes: any[] = [];
        const facilities = (this.applicationForm.get('facilities') as FormArray)
          .value;
        facilities.forEach((facility: any, index: number, array: []) => {
          facilityFoodTypes.push({
            identifier: facility.identifier,
            foodTypes: Array.from(this.finallySelectedFoodTypes.get(index)),
          });
        });
        this.recordService
          .approveRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            facilityFoodTypes
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

  private getWaterSupplyTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02400')
      .subscribe((response: NomenclatureInterface[]) => {
        this.waterSupplyTypes = response;
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

  onToggleChange(
    data: { foodType: FoodTypeInterface; checked: boolean },
    facilityIndex: number
  ) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    let foundFoods = this.finallySelectedFoodTypes.get(facilityIndex);
    if (!isChecked) {
      foundFoods.forEach((foundFood: FoodTypeInterface) => {
        if (foundFood.code === code) {
          foundFoods.delete(foundFood);
        }
      });
    } else {
      foundFoods.add(foodType);
    }
  }
}
