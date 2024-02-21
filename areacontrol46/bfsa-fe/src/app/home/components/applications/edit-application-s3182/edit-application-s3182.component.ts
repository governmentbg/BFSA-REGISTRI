import { Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { EditApplicationS3181Component } from '../edit-application-s3181/edit-application-s3181.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { InspectionDialogComponent } from 'src/app/home/inspection/components/inspection-dialog/inspection-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { AddressService } from 'src/app/services/address.service';
import { DatePipe } from '@angular/common';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';

@Component({
  selector: 'app-edit-application-s3182',
  templateUrl: './edit-application-s3182.component.html',
  styleUrls: ['./edit-application-s3182.component.scss'],
})
export class EditApplicationS3182Component {
  public applicationForm: FormGroup;
  public areaSettlements: SettlementInterface[];
  public branches: any[];
  public activityTypes: any[];
  public measurementUnits: NomenclatureInterface[];
  public materialTypes: NomenclatureInterface[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[];
  public displayUsingFoodTransportByClause52: boolean = false;
  public displayFacilityCapacities: boolean = false;
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public countries: any[];
  public waterSupplyTypes: NomenclatureInterface[];
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder,
    private settlementService: SettlementService,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS3181Component>,
    private readonly branchService: BranchService,
    private nomenclatureService: NomenclatureService,
    private recordService: RecordService,
    private addressService: AddressService,
    private datePipe: DatePipe,
    public refusalDialog: MatDialog
  ) {
    // this.dateAdapter.setLocale('en-GB');
  }
  ngOnInit() {
    // this.getParentActivityGroups();
    // this.getAllPictograms();
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();
    this.getWaterSupplyTypes();
    this.getActivityTypes();
    this.getMeasuringUnits();
    this.getMaterialTypes();

    if (this.data) {
      this.data.commencementActivityDate = this.datePipe.transform(
        this.data.commencementActivityDate,
        'dd-MM-yyyy'
      );
      this.buildForm();
      this.applicationForm.disable();

      if (this.data.vehicles?.length) {
        this.displayUsingFoodTransportByClause52 = true;
        this.fillVehicles();
      }

      if (this.data.facility?.facilityCapacities?.length) {
        this.displayFacilityCapacities = true;
        this.fillFacilityCapacities();
      }
    }
  }

  buildForm() {
    this.applicationForm = this.fb.group({
      recordId: [this.data.id],
      address: this.fb.group({
        phone: [{ value: this.data.address?.phone, disabled: true }],
        mail: [{ value: this.data.address?.mail, disabled: true }],
        url: [{ value: this.data.address?.url, disabled: true }],
        name: [{ value: this.data.address?.name, disabled: true }],
        fullAddress: [{ value: this.data.address?.fullAddress, disabled: true }],
      }),

      commencementActivityDate: [
        { value: this.data.commencementActivityDate, disabled: true },
      ],
      settlementCode: [
        { value: this.data.address?.settlementCode, disabled: true },
      ],
      phone: [{ value: this.data.phone, disabled: true }],
      mail: [{ value: this.data.mail, disabled: true }],
      url: [{ value: this.data.url, disabled: true }],
      branchIdentifier: [{ value: this.data.branchIdentifier, disabled: true }],
      vehicles: this.fb.array([]),
      settlementAddressFull: [{ value: '', disabled: true }],
      facility: this.fb.group({
        permission177: [
          { value: this.data.facility?.permission177, disabled: true },
        ],
        fullAddress: [{ value: this.data.facility?.address?.fullAddress, disabled: true }],
        activityDescription: [
          { value: this.data.facility?.activityDescription, disabled: true },
        ],
        activityTypeCode: [
          { value: this.data.facility?.activityTypeCode, disabled: true },
        ],
        address: [{ value: this.data.facility?.address, disabled: true }],
        waterSupplyTypeCode: [
          { value: this.data.facility?.waterSupplyTypeCode, disabled: true },
        ],
        disposalWasteWater: [
          { value: this.data.facility?.disposalWasteWater, disabled: true },
        ],
        name: [{ value: this.data.facility?.name, disabled: true }],
        facilityCapacities: this.fb.array([]),
      }),
    });
  }

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
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
  //ForCorrectionDialogComponent

  fillForeignFacilityAdresses() {
    this.data.foreignFacilityAddresses.map((el: any, index: number) => {
      const foreignFacilityForm = this.fb.group({
        countryCode: [{ value: el.countryCode, disabled: true }],
        address: [{ value: el.address, disabled: true }],
      });
      this.foreignFacilityAddresses.push(foreignFacilityForm);
    });
  }

  fillVehicles() {
    this.data.vehicles.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        vehicleOwnershipTypeCode: [
          { value: el.vehicleOwnershipTypeCode, disabled: true },
        ],
        vehicleTypeCode: [{ value: el.vehicleTypeCode, disabled: true }],
        registrationPlate: [{ value: el.registrationPlate, disabled: true }],
        brandModel: [{ value: el.brandModel, disabled: true }],
      });
      //console.log(this.vehicles);
      this.vehicles.push(vehicleForm);
    });
  }

  fillFacilityCapacities() {
    this.data.facility.facilityCapacities.map((el: any, index: number) => {
      const facilityCapacitiesForm = this.fb.group({
        product: [{ value: el.product, disabled: true }],
        materialCode: [{ value: el.materialCode, disabled: true }],
        quantity: [{ value: el.quantity, disabled: true }],
        unitCode: [{ value: el.unitCode, disabled: true }],
      });
      // console.log(this.facilityCapacities);
      this.facilityCapacities.push(facilityCapacitiesForm);
    });
  }

  fillFacilityPaperNumbers() {
    this.data.facilitiesPaperNumbers.map((el: any, index: number) => {
      const facilityPaperForm = this.fb.group({
        facilityPaperNumber: [{ value: el, disabled: true }],
      });
      this.facilitiesPaperNumbers.push(facilityPaperForm);
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

  removeVehicle(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.vehicles.removeAt(index);
        }
      });
  }

  removeForeignFacilityAddress(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.foreignFacilityAddresses.removeAt(index);
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

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
      });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measurementUnits = response;
      });
  }

  private getMaterialTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02300')
      .subscribe((response: NomenclatureInterface[]) => {
        //   console.log(response);
        this.materialTypes = response;
      });
  }

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
  }

  get isStatusRejectedOrApproved() {
    return (
      this.data?.recordStatus === 'FINAL_REJECTED' ||
      this.data?.recordStatus === 'FINAL_APPROVED'
    );
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

  removeFacilityPaperNumbers(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.facilitiesPaperNumbers.removeAt(index);
        }
      });
  }

  public openInspectionDialog() {
    const dialogRef = this.dialog.open(InspectionDialogComponent, {
      width: '500px',
      data: this.applicationForm.getRawValue(),
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.dialogRef.close(true);
    });
  }

  removech50VehicleCertNumbers(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.ch50VehicleCertNumbers.removeAt(index);
        }
      });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  get vehicles() {
    return this.applicationForm.controls['vehicles'] as FormArray;
  }

  get facilityCapacities() {
    return this.applicationForm
      .get('facility')
      ?.get('facilityCapacities') as FormArray;
  }

  get facilitiesPaperNumbers() {
    return this.applicationForm.controls['facilitiesPaperNumbers'] as FormArray;
  }

  get ch50VehicleCertNumbers() {
    return this.applicationForm.controls['ch50VehicleCertNumbers'] as FormArray;
  }

  get foreignFacilityAddresses() {
    return this.applicationForm.controls[
      'foreignFacilityAddresses'
    ] as FormArray;
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }
}
