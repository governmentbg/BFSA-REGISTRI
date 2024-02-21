import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import {
  ActivityGroupInterface,
  AnimalSpecie,
  Remark,
  RelatedActivityCategory,
  AssociatedActivityCategory,
} from 'src/app/home/activity-group/interfaces/activity-group-interface';
import { CountryService } from 'src/app/home/inspection/services/country.service';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-create-application-s3181',
  templateUrl: './create-application-s3181.component.html',
  styleUrls: ['./create-application-s3181.component.scss'],
})
export class CreateApplicationS3181Component {
  public ch50VehicleCertNumbersArr: any[] = [];
  public areEmailsEqual: boolean;
  public activityGroupParents: ActivityGroupInterface[];
  public branches: any[];
  public animalSpecies: AnimalSpecie[];
  public remarks: Remark[];
  public selectedRelatedActivityCategories: any[];
  public relatedActivityCategories: RelatedActivityCategory[];
  public associatedActivityCategories: AssociatedActivityCategory[];
  public subActivityGroups: ActivityGroupInterface[];
  public pictograms: any[];
  public requestorAuthorTypes: NomenclatureInterface[];
  // TODO to set a type on all ^
  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[] | null;
  public facilityStatuses = Object.entries(recordStatus);
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];
  public displayRegisteredFacilityInBulgaria: boolean = false;
  public displayRegisteredFacilityOutsideOfBulgaria: boolean = false;

  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public countries: any[];

  public facilitiesPaperNumbersArr: any[] = [];
  public vehiclesArr: any[] = [];
  public foreignFacilityAddresses: any[] = [];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS3181Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly countryService: CountryService,
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
    serviceType: ['S3181'],
    address: this.fb.group({
      settlementCode: [null],
      address: ['test'],
      phone: ['123-123-123'],
      url: ['test'],
      mail: [`test-${Math.random().toString(16).slice(2, 8)}@mail.com`],
    }),

    branchIdentifier: ['176986657'],
    facilitiesPaperNumbers: [null],
    foreignFacilityAddresses: this.fb.group({
      countryCode: [''],
      address: [''],
    }),
    vehicles: this.fb.group({
      vehicleOwnershipTypeCode: [null],
      vehicleTypeCode: [null],
      brandModel: [null],
      registrationPlate: [null],
    }),
    ch50VehicleCertNumbers: [''],

    commencementActivityDate: [null],
    facilityZone: [null, Validators.required],
    facilityMunicipality: [null, Validators.required],
    registeredFacilityInBulgaria: [false],
    registeredFacilityOutsideOfBulgaria: [false],
    usingFoodTransportByClause52: [false],
    usingFoodTransportByClause50: [false],

    applicantCorrespondenceAddress: this.fb.group({
      address: ['', Validators.required],
      addressTypeCode: [
        ApplicationsAddressTypeCodes.CORRESPONDENCE_ADDRESS_TYPE_CODE,
      ],
      settlementCode: ['', Validators.required],
      postCode: ['', Validators.required],
    }),
  });

  correspondenceMunicipalitySettlements: SettlementInterface[];
  correspondenceSettlements: SettlementInterface[] | null;

  onChangeHeadOfficeZone(id: string) {
    this.settlementService.getMunicipalitiesByCode(id).subscribe((res) => {
      this.municipalitySettlements = res;
      this.settlements = null;
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
      this.correspondenceSettlements = null;
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

  ngOnInit() {
    this.settlementService.getMunicipalitiesByCode('07079').subscribe((res) => {
      this.municipalitySettlements = res;
    });
    this.settlementService.getMunicipalitiesByCode('07079').subscribe((res) => {
      this.settlements = res;
    });
    this.applicationForm.get('requestorCorrespondenceAddress')?.patchValue({
      address: 'test',
      settlementCode: '07079',
      postCode: '123',
    });
    this.settlementService.getMunicipalitiesByCode('07079').subscribe((res) => {
      this.correspondenceMunicipalitySettlements = res;
    });
    this.settlementService.getRegionsByCode('07079').subscribe((res) => {
      this.correspondenceSettlements = res;
    });
    this.applicationForm.get('applicantCorrespondenceAddress')?.patchValue({
      address: 'test',
      settlementCode: '07079',
      postCode: '123',
    });

    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getAllCountries();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();

    this.applicationForm.get('facilityZone')?.valueChanges.subscribe((val) =>
      this.settlementService.getMunicipalitiesByCode(val).subscribe((res) => {
        this.facilityMunicipalities = res;
      })
    );

    this.applicationForm
      .get('facilityMunicipality')
      ?.valueChanges.subscribe((val) =>
        this.settlementService.getRegionsByCode(val).subscribe((res) => {
          this.facilitySettlements = res;
        })
      );
    this.applicationForm.get('applicantZone')?.valueChanges.subscribe((val) =>
      this.settlementService.getMunicipalitiesByCode(val).subscribe((res) => {
        this.municipalitySettlements = res;
      })
    );

    this.applicationForm
      .get('applicantMunicipality')
      ?.valueChanges.subscribe((val) =>
        this.settlementService.getRegionsByCode(val).subscribe((res) => {
          this.settlements = res;
        })
      );

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
      .get('registeredFacilityInBulgaria')
      ?.valueChanges.subscribe((val) => {
        this.displayRegisteredFacilityInBulgaria = val;
      });
    this.applicationForm
      .get('registeredFacilityOutsideOfBulgaria')
      ?.valueChanges.subscribe((val) => {
        this.displayRegisteredFacilityOutsideOfBulgaria = val;
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

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
    });
  }

  addVehicle() {
    this.vehiclesArr.push(this.applicationForm.get('vehicles')?.value);
    this._snackBar.open('Vehicle added', '', {
      duration: 1000,
    });
    console.log(this.vehiclesArr);
  }

  addFacilityPaper() {
    this.facilitiesPaperNumbersArr.push(
      this.applicationForm.get('facilitiesPaperNumbers')?.value
    );
    this._snackBar.open('facility Paper added', '', {
      duration: 1000,
    });
    console.log(this.facilitiesPaperNumbersArr);
  }

  addForeignFacilityAddress() {
    console.log(this.applicationForm.get('foreignFacilityAddresses')?.value);
    this.foreignFacilityAddresses.push(
      this.applicationForm.get('foreignFacilityAddresses')?.value
    );
    this._snackBar.open('Foreign facility adress added', '', {
      duration: 1000,
    });
    console.log(this.foreignFacilityAddresses);
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

  createApplication() {
    console.log(this.applicationForm.value);
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  private getAllCountries() {
    this.countryService.getAllCountries().subscribe((res: any) => {
      this.countries = res.content;
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

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  registerApplication() {
    console.log(this.applicationForm.value);

    const dto = this.applicationForm.value;
    dto.facilitiesPaperNumbers = this.facilitiesPaperNumbersArr;
    dto.foreignFacilityAddresses = this.foreignFacilityAddresses;
    dto.vehicles = this.vehiclesArr;
    dto.ch50VehicleCertNumbers = this.ch50VehicleCertNumbersArr;

    this.recordService.registerApplicationS3181(dto).subscribe((res) => {
      console.log('in response', res);

      this.dialogRef.close(res);
    });
  }
}
