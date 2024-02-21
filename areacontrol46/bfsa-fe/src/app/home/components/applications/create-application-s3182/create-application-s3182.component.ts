import { ChangeDetectorRef, Component, Inject } from '@angular/core';
import { UntypedFormBuilder, Validators, FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import {
  ActivityGroupInterface,
  AnimalSpecie,
  Remark,
  RelatedActivityCategory,
  AssociatedActivityCategory,
} from 'src/app/home/activity-group/interfaces/activity-group-interface';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';
import { CountryService } from 'src/app/home/inspection/services/country.service';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { CreateApplicationS3181Component } from '../create-application-s3181/create-application-s3181.component';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-create-application-s3182',
  templateUrl: './create-application-s3182.component.html',
  styleUrls: ['./create-application-s3182.component.scss'],
})
export class CreateApplicationS3182Component {
  public activityTypes: NomenclatureInterface[];
  public materialTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public measurementUnits: NomenclatureInterface[];
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
  public displayUsingFoodTransportByClause52: boolean = false;
  public displayUsingFoodTransportByClause50: boolean = false;
  public vehicleOwnerships: any[];
  public vehicleTypes: any[];
  public countries: any[];

  public productionCode = '01806';
  public commerceCode = '01807';
  public milkProductsCode = '01808';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS3181Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly countryService: CountryService,
    private _snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
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
    address: this.fb.group({
      phone: [''],
      mail: [''],
      url: [''],
      postCode: [''],
      settlementCode: [''],
      address: [''],

      facilityZone: [null],
      facilityMunicipality: [null],
      //ui only  ^
    }),

    branchIdentifier: ['176986657'],
    applicantAddresses: this.fb.array([]),
    serviceType: ['S3182'],
    vehicles: this.fb.array([]),
    commencementActivityDate: [null, Validators.required],
    registeredFacilityInBulgaria: [false],
    registeredFacilityOutsideOfBulgaria: [false],
    usingFoodTransportByClause52: [false],
    usingFoodTransportByClause50: [false],
    remoteDistance: [false],

    facility: this.fb.group({
      permission177: [''],
      settlementCode: [''],
      activityDescription: [''],
      activityTypeCode: [''],
      waterSupplyTypeCode: [''],
      disposalWasteWater: [''],
      name: [''],
      facilityCapacities: this.fb.array([
        this.fb.group({
          product: [null],
          materialCode: [null],
          quantity: [null],
          unitCode: [null],
        }),
      ]),
      address: this.fb.group({
        address: ['asd'],
        postCode: [''],
        settlementCode: [''],
        // ui only
      }),
    }),
    // product: [''],
    // materialCode: [''],
    // quantity: [''],
    // unitCode: [''],

    applicantCorrespondenceAddress: this.fb.group({
      address: ['', Validators.required],
      addressTypeCode: [
        ApplicationsAddressTypeCodes.CORRESPONDENCE_ADDRESS_TYPE_CODE,
      ],
      settlementCode: ['', Validators.required],
      postCode: ['', Validators.required],
    }),
  });

  get applicantAddresses() {
    return this.applicationForm.controls['applicantAddresses'] as FormArray;
  }

  get remoteDistance() {
    return this.applicationForm.get('remoteDistance')?.value;
  }

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
      this.cdr.detectChanges();
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
      this.cdr.detectChanges();
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
      this.cdr.detectChanges();
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
      this.cdr.detectChanges();
    });
  }

  ngOnInit() {
    this.applicationForm.get('address')?.disable();
    this.applicationForm
      .get('remoteDistance')
      ?.valueChanges.subscribe((val) => {
        if (val) {
          this.applicationForm.get('address')?.enable();
          this.cdr.detectChanges();
        } else {
          this.applicationForm.get('address')?.disable();
          this.cdr.detectChanges();
        }
      });

    this.applicationForm
      .get('usingFoodTransportByClause52')
      ?.valueChanges.subscribe((val) => this.cdr.detectChanges());

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

    this.getMaterialTypes();
    this.getWaterSupplyTypes();
    this.getActivityTypes();
    this.getMeasuringUnits();

    this.applicationForm
      ?.get('address')
      ?.get('facilityZone')
      ?.valueChanges.subscribe((val) =>
        this.settlementService.getMunicipalitiesByCode(val).subscribe((res) => {
          this.facilityMunicipalities = res;
        })
      );

    this.applicationForm
      .get('address')
      ?.get('facilityMunicipality')
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
      ?.valueChanges.subscribe((val: boolean) => {
        this.displayRegisteredFacilityInBulgaria = val;
      });
    this.applicationForm
      .get('registeredFacilityOutsideOfBulgaria')
      ?.valueChanges.subscribe((val: boolean) => {
        this.displayRegisteredFacilityOutsideOfBulgaria = val;
      });
    this.applicationForm
      .get('usingFoodTransportByClause52')
      ?.valueChanges.subscribe((val: boolean) => {
        this.displayUsingFoodTransportByClause52 = val;
      });
    this.applicationForm
      .get('usingFoodTransportByClause50')
      ?.valueChanges.subscribe((val: boolean) => {
        this.displayUsingFoodTransportByClause50 = val;
      });
  }

  get vehicles() {
    return this.applicationForm?.controls['vehicles'] as FormArray;
  }

  get facilityCapacities() {
    return this.applicationForm
      ?.get('facility')
      ?.get('facilityCapacities') as FormArray;
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
    this.settlementService
      .getParentSettlements()
      .subscribe((response: SettlementInterface[]) => {
        this.areaSettlements = response;
      });
  }

  private getMaterialTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02300')
      .subscribe((response: NomenclatureInterface[]) => {
        console.log(response);
        this.materialTypes = response;
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

  addVehicle() {
    const vehicleForm = this.fb.group({
      vehicleOwnershipTypeCode: [null],
      vehicleTypeCode: [null],
      brandModel: [null],
      registrationPlate: [null],
    });

    this.vehicles.push(vehicleForm);
  }

  addFacility() {
    const facilityForm = this.fb.group({
      product: [null],
      materialCode: [null],
      quantity: [null],
      unitCode: [null],
    });
    this.facilityCapacities.push(facilityForm);

    // this.applicationForm.get('product')?.setValue('');
    // this.applicationForm.get('material')?.setValue('');
    // this.applicationForm.get('quantity')?.setValue('');
    // this.applicationForm.get('unit')?.setValue('');
    //setvalue '' ^
    this._snackBar.open('Facility added', '', {
      duration: 1000,
    });
  }

  private getAllBranches() {
    this.branchService
      .getBranches()
      .subscribe((response: { results: any[] }) => {
        this.branches = response.results;
      });
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res: any[]) => {
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
      .subscribe((res: any[]) => {
        this.vehicleOwnerships = res;
      });
  }

  private getAllVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res: any[]) => {
        this.vehicleTypes = res;
      });
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  registerApplication() {
    const dto = this.applicationForm.value;
    console.log(dto);
    this.recordService.registerApplicationS3182(dto).subscribe((res) => {
      console.log('in response', res);

      this.dialogRef.close(res);
    });
  }
}
