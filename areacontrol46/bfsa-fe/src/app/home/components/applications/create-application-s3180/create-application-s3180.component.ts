import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import {
  ActivityGroupInterface,
  AnimalSpecie,
  AssociatedActivityCategory,
  RelatedActivityCategory,
  Remark,
} from 'src/app/home/activity-group/interfaces/activity-group-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-create-application-s3180',
  templateUrl: './create-application-s3180.component.html',
  styleUrls: ['./create-application-s3180.component.scss'],
})
export class CreateApplicationS3180Component {
  public activityTypes: NomenclatureInterface[];
  public subActivityTypes: NomenclatureInterface[];
  public periods: NomenclatureInterface[];
  public rawMilkTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public measuringUnits: NomenclatureInterface[];
  public activityGroupParents: ActivityGroupInterface[];
  public subActivityGroups: ActivityGroupInterface[];
  public relatedActivityCategories: RelatedActivityCategory[];
  public associatedActivityCategories: AssociatedActivityCategory[];
  public animalSpecies: AnimalSpecie[];
  public facilityCapacities: any[] = [];
  public vehiclesArr: any[] = [];
  public ch50VehicleCertNumbersArr: any[] = [];

  public areEmailsEqual: boolean;
  public branches: BranchInterface[];
  public remarks: Remark[];
  public selectedRelatedActivityCategories: any[];
  public pictograms: any[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public displaySubActivityTypes: boolean = false;
  public displayExtraFacilityInformation: boolean = false;
  public displayMilkProductsInformation = false;
  public nomenclatureCodes: string[] = [];

  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];

  public productionCode = '01806';
  public commerceCode = '01807';
  public milkProductsCode = '01808';

  // TODO to set a type on all ^
  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[] | null;
  public facilityStatuses = Object.entries(recordStatus);
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS3180Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private _snackBar: MatSnackBar
  ) {}

  public applicationForm = this.fb.group({
    requestorFullName: ['test', [Validators.required]],
    requestorIdentifier: [
      Math.floor(Math.random() * 10000000000),
      [Validators.required],
    ],
    requestorEmail: [
      `test-${Math.random().toString(16).slice(2, 8)}@mail.com`,
      [Validators.required],
    ],
    requestorAuthorTypeCode: ['01301', [Validators.required]],
    requestorPowerAttorneyNumber: [''],
    requestorPowerAttorneyNotary: [''],
    requestorPowerAttorneyDate: [''],
    requestorPowerAttorneyUntilDate: [''],

    entityType: ['LEGAL', [Validators.required]],
    applicantFullName: ['test', [Validators.required]],
    applicantIdentifier: [
      Math.floor(Math.random() * 10000000000),
      [Validators.required],
    ],
    applicantEmail: [
      `test-${Math.random().toString(16).slice(2, 8)}@mail.com`,
      Validators.required,
    ],
    applicantPostCode: ['123', Validators.required],
    applicantAddress: ['test', Validators.required],
    applicantPhone: ['123-123-123', Validators.required],

    applicantCorrespondenceAddress: this.fb.group({
      address: ['asdasd', Validators.required],
      addressTypeCode: [
        ApplicationsAddressTypeCodes.CORRESPONDENCE_ADDRESS_TYPE_CODE,
      ],
      settlementCode: ['', Validators.required],
      postCode: ['', Validators.required],
    }),

    commencementActivityDate: [null],
    serviceType: ['S3180'],
    facility: this.fb.group({
      permission177: [''],
      activityDescription: [''],
      activityTypeCode: [''],
      subActivityTypeCode: [''],
      waterSupplyTypeCode: [''],
      disposalWasteWater: [''],
      name: [''],
      measuringUnitCode: [''],
      periodCode: [''],
      capacity: [''],
      address: this.fb.group({
        address: ['asd'],
        postCode: [''],
        phone: [''],
        settlementCode: [''],
      }),
    }),
    branchIdentifier: ['176986657'],

    address: this.fb.group({
      settlementCode: [''],
      address: [''],
      phone: [''],
      url: [''],
      mail: [``],
    }),

    remoteDistance: [false],

    usingFoodTransportByClause52: [false],
    usingFoodTransportByClause50: [false],

    vehicles: this.fb.group({
      vehicleOwnershipTypeCode: [null],
      vehicleTypeCode: [null],
      brandModel: [null],
      registrationPlate: [null],
    }),
    ch50VehicleCertNumbers: [''],
    capacityForm: this.fb.group({
      rawMilkTypeCode: [null],
      fridgeCapacity: [null],
    }),
  });

  correspondenceMunicipalitySettlements: SettlementInterface[];
  correspondenceSettlements: SettlementInterface[] | null;

  onChangeHeadOfficeZone(id: string) {
    this.settlementService.getMunicipalitiesByCode(id).subscribe((res) => {
      this.municipalitySettlements = res;
      //this.settlements = null;
      this.applicationForm
        .get('requestorCorrespondenceAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('requestorCorrespondenceAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeHeadOfficeMunicipalitySettlements(id: string) {
    this.settlementService.getMunicipalitiesByCode(id).subscribe((res) => {
      this.settlements = res;
      this.applicationForm
        .get('requestorCorrespondenceAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('requestorCorrespondenceAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeCorrespondenceZone(id: string) {
    this.settlementService.getMunicipalitiesByCode(id).subscribe((res) => {
      this.correspondenceMunicipalitySettlements = res;
      //  this.correspondenceSettlements = null;
      this.applicationForm
        .get('applicantCorrespondenceAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('applicantCorrespondenceAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeCorrespondenceMunicipalitySettlements(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.correspondenceSettlements = res;
      this.applicationForm
        .get('applicantCorrespondenceAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('applicantCorrespondenceAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeFacilityZone(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.facilitySettlements = res;
      this.applicationForm
        .get('facility')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('facility')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeFacilityMunicipalitySettlements(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.facilitySettlements = res;
      this.applicationForm
        .get('facility')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('facility')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeRemoteAddressFacilityZone(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.facilitySettlements = res;
      this.applicationForm
        .get('address')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('address')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeRemoteAddressFacilityMunicipalitySettlements(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.facilitySettlements = res;
      this.applicationForm
        .get('address')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('address')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getActivityTypes();
    this.getSubActivityTypes();
    this.getWaterSupplyTypes();
    this.getMeasuringUnits();
    this.getPeriods();
    this.getRawMilkTypes();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();

    this.settlementService.getMunicipalitiesByCode('07079').subscribe((res) => {
      this.municipalitySettlements = res;
      this.settlements = res;
      this.facilityMunicipalities = res;
      this.correspondenceMunicipalitySettlements = res;
    });
    this.applicationForm.get('requestorCorrespondenceAddress')?.patchValue({
      address: 'test',
      settlementCode: '07079',
      postCode: '123',
    });
    this.settlementService.getRegionsByCode('07079').subscribe((res) => {
      this.correspondenceSettlements = res;
    });
    this.applicationForm.get('applicantCorrespondenceAddress')?.patchValue({
      address: 'test',
      settlementCode: '07079',
      postCode: '123',
    });

    this.applicationForm
      .get('facility')
      ?.get('activityTypeCode')
      ?.valueChanges.subscribe((val) => {
        const production = '01806';
        const commerce = '01807';
        const milkProducts = '01808';

        if (val === commerce) {
          this.displaySubActivityTypes = true;
          this.displayExtraFacilityInformation = true;
          this.displayMilkProductsInformation = false;
        } else if (val === production) {
          this.displayExtraFacilityInformation = true;
          this.displaySubActivityTypes = false;
          this.displayMilkProductsInformation = false;
        } else if (val === milkProducts) {
          this.displayExtraFacilityInformation = false;
          this.displaySubActivityTypes = false;
          this.displayMilkProductsInformation = true;
        } else {
          this.displayExtraFacilityInformation = false;
          this.displaySubActivityTypes = false;
          this.displayMilkProductsInformation = false;
        }
      });

    this.applicationForm
      .get('requestorAuthorTypeCode')
      ?.valueChanges.subscribe((val) => console.log(val));

    this.applicationForm
      .get('requestorEmail')
      ?.valueChanges.subscribe((val) => {
        if (this.applicationForm.get('applicantEmail')?.value === val) {
          this.applicationForm.controls['requestorEmail'].setErrors({
            incorrect: true,
          });
          this.applicationForm.controls['applicantEmail'].setErrors({
            incorrect: true,
          });
        } else {
          this.applicationForm.controls['requestorEmail'].setErrors(null);
          this.applicationForm.controls['applicantEmail'].setErrors(null);
          this.applicationForm.updateValueAndValidity();
        }
      });

    this.applicationForm
      .get('applicantEmail')
      ?.valueChanges.subscribe((val) => {
        if (this.applicationForm.get('requestorEmail')?.value === val) {
          this.applicationForm.controls['requestorEmail'].setErrors({
            incorrect: true,
          });
          this.applicationForm.controls['applicantEmail'].setErrors({
            incorrect: true,
          });
        } else {
          this.applicationForm.controls['applicantEmail'].setErrors(null);
          this.applicationForm.controls['requestorEmail'].setErrors(null);
          this.applicationForm.updateValueAndValidity();
        }
      });

    this.applicationForm
      .get('remoteDistance')
      ?.valueChanges.subscribe((val) => {
        if (val) {
          this.applicationForm.get('address')?.enable();
        } else {
          this.applicationForm.get('address')?.disable();
        }
      });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  addDays(date: Date, days: number) {
    let result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  addfacilityCapacities() {
    const facilityCapacitiesDTO = {
      rawMilkTypeCode: this.applicationForm
        .get('capacityForm')
        ?.get('rawMilkTypeCode')?.value,
      fridgeCapacity: this.applicationForm
        .get('capacityForm')
        ?.get('fridgeCapacity')?.value,
    };
    this.facilityCapacities.push(facilityCapacitiesDTO);

    this._snackBar.open('facility capacity added', '', {
      duration: 1000,
    });
  }

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
    });
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
      });
  }

  private getSubActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01100')
      .subscribe((response: NomenclatureInterface[]) => {
        this.subActivityTypes = response;
      });
  }

  private getWaterSupplyTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02400')
      .subscribe((response: NomenclatureInterface[]) => {
        this.waterSupplyTypes = response;
      });
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
      });
  }

  private getPeriods() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.periods = response;
      });
  }

  private getRawMilkTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.rawMilkTypes = response;
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

  addVehicle() {
    this.vehiclesArr.push(this.applicationForm.get('vehicles')?.value);
    this._snackBar.open('Vehicle added', '', {
      duration: 1000,
    });
    console.log(this.vehiclesArr);
  }

  addVehiclePaperRegNumber() {
    this.ch50VehicleCertNumbersArr.push(
      this.applicationForm.get('ch50VehicleCertNumbers')?.value
    );
    this._snackBar.open('Vehicle Paper RegNumber added', '', {
      duration: 1000,
    });
    console.log(this.ch50VehicleCertNumbersArr);
  }

  // private getNomenclatureByCode(code: string) {
  //   this.nomenclatureService.getNomenclatureByParentCode(code);
  // }
  //todo gather all codes into nomenclArr and loop it in oninit (from ext. function) to fill datasources

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  registerApplication() {
    const dto = this.applicationForm.value;

    dto.vehicles = this.vehiclesArr;
    dto.ch50VehicleCertNumbers = this.ch50VehicleCertNumbersArr;
    dto.facility.facilityCapacities = this.facilityCapacities;

    console.log(this.applicationForm.value);

    this.recordService
      .registerApplicationS3180(this.applicationForm.value)
      .subscribe((res) => {
        console.log('in response', res);

        this.dialogRef.close(res);
      });
    //on error don't close
  }
}
