import { DatePipe } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import * as saveAs from 'file-saver';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { DocumentService } from 'src/app/services/document.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-edit-application-s7691',
  templateUrl: './edit-application-s7691.component.html',
  styleUrls: ['./edit-application-s7691.component.scss'],
})
export class EditApplicationS7691Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public requestorAuthorTypes: NomenclatureInterface[];
  public finallySelectedPrimaryProductFoodTypes: { code: string }[] = [];
  public finallySelectedProducedFoodTypes: { code: string }[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS7691Component>,
    public refusalDialog: MatDialog,
    private recordService: RecordService,
    private fb: UntypedFormBuilder,
    private datePipe: DatePipe,
    private readonly documentService: DocumentService
  ) {}

  get fishingVessels() {
    return this.applicationForm.controls['fishingVessels'] as FormArray;
  }

  ngOnInit() {
    if (this.data.primaryProductFoodTypes) {
      this.data?.primaryProductFoodTypes?.map((el: FoodTypeInterface) => {
        // fill new array to break reference from array
        this.finallySelectedPrimaryProductFoodTypes.push({ code: el.code });
      });
    }
    if (this.data?.producedFoodTypes) {
      this.data?.producedFoodTypes?.map((el: FoodTypeInterface) => {
        this.finallySelectedProducedFoodTypes.push({ code: el.code });
      });
    }

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
      facility: this.fb.group({
        activityTypeName: [this.data.facility?.activityTypeName],
        name: [this.data.facility?.name],
        fullAddress: [this.data.facility?.address?.fullAddress],
        regNumber: [this.data.facility?.regNumber],
        periodName: [this.data.facility?.periodName],
      }),

      primaryProductFoodTypes: [this.data.primaryProductFoodTypes],
      primaryProductFoodTypeDescription: [
        this.data.primaryProductFoodTypeDescription,
      ],
      primaryProductLiveStockRegNumber: [
        this.data.primaryProductLiveStockRegNumber,
      ],
      primaryProductFarmerIdentifier: [
        this.data.primaryProductFarmerIdentifier,
      ],

      producedFoodTypes: [this.data.producedFoodTypes],
      producedFoodTypeDescription: [this.data.producedFoodTypeDescription],
      producedFoodAnnualPeriodName: [this.data.producedFoodAnnualPeriodName],
      producedFoodEstimatedAnnual: [this.data.producedFoodEstimatedAnnual],
      producedFoodEstimatedAnnualUnitName: [
        this.data.producedFoodEstimatedAnnualUnitName,
      ],

      primaryProductEstimatedAnnualYield: [
        this.data.primaryProductEstimatedAnnualYield,
      ],
      foodAnnualCapacity: [this.data.foodAnnualCapacity],
      foodAnnualCapacityUnitName: [this.data.foodAnnualCapacityUnitName],
      foodPeriodName: [this.data.foodPeriodName],

      huntingPartyBranch: [this.data.huntingPartyBranch],
      areaHuntingAreas: [this.data.areaHuntingAreas],
      locationGameProcessingFacility: [
        this.data.locationGameProcessingFacility,
      ],
      gameStationCapacity: [this.data.gameStationCapacity],
      gameStationCapacityUnitName: [this.data.gameStationCapacityUnitName],
      gameProcessingPeriodName: [this.data.gameProcessingPeriodName],
      responsiblePeople: [this.data.responsiblePeople],
      gameType: [this.data.gameType],
      permittedExtraction: [this.data.permittedExtraction],
      fishingVessels: this.fb.array([]),

      deliveryLiveStockRegNumber: [this.data.deliveryLiveStockRegNumber],
      deliveryFarmerIdentifier: [this.data.deliveryFarmerIdentifier],
      deliveryAddressLivestockFacility: [
        this.data.deliveryAddressLivestockFacility,
      ],
      deliveryFacilityCapacity: [this.data.deliveryFacilityCapacity],
      deliveryFacilityCapacityUnitName: [
        this.data.deliveryFacilityCapacityUnitName,
      ],
      deliveryPeriodName: [this.data.deliveryPeriodName],

      address: this.fb.group({
        fullAddress: this.data?.address?.fullAddress,
        phone: this.data?.address?.phone,
        mail: this.data?.address?.mail,
        url: this.data?.address?.url,
      }),
      commencementActivityDate: [this.data.commencementActivityDate],
    });
    if (this.data.fishingVessels?.length) {
      this.fillFishingVessels();
    }
    this.applicationForm.disable();
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

  fillFishingVessels() {
    this.data.fishingVessels.map((el: any, index: number) => {
      const vesselForm = this.fb.group({
        regNumber: [{ value: el.regNumber, disabled: true }],
        externalMarking: [{ value: el.externalMarking, disabled: true }],
        typeName: [{ value: el.typeName, disabled: true }],
        assignmentTypeName: [{ value: el.assignmentTypeName, disabled: true }],
        hullLength: [{ value: el.hullLength, disabled: true }],
      });
      this.fishingVessels.push(vesselForm);
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
        const selectedFoodTypes = [
          {
            identifier: 'productFoodTypes',
            foodTypes: this.finallySelectedPrimaryProductFoodTypes,
          },
          {
            identifier: 'producedFoodTypes',
            foodTypes: this.finallySelectedProducedFoodTypes,
          },
        ];

        this.recordService
          .approveRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            selectedFoodTypes
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

  onToggleChangePrimaryProduct(data: { foodType: any; checked: boolean }) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    if (!isChecked) {
      this.finallySelectedPrimaryProductFoodTypes.map(
        (el: { code: string }, index: number) => {
          if (el.code === code) {
            this.finallySelectedPrimaryProductFoodTypes.splice(index, 1);
          }
          return el;
        }
      );
    } else {
      this.finallySelectedPrimaryProductFoodTypes.push({ code: foodType.code });
    }
    return;
  }

  onToggleChangeProducedFoodTypes(data: { foodType: any; checked: boolean }) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    if (!isChecked) {
      this.finallySelectedProducedFoodTypes.map(
        (el: { code: string }, index: number) => {
          if (el.code === code) {
            this.finallySelectedProducedFoodTypes.splice(index, 1);
          }
          return el;
        }
      );
    } else {
      this.finallySelectedProducedFoodTypes.push({ code: foodType.code });
    }
    return;
  }
}
