import { Component } from '@angular/core';
import {
  FormArray,
  FormGroup,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { BranchService } from 'src/app/services/branch.service';
import { BranchInterface } from 'src/app/home/contractor/interfaces/branch-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { RecordService } from 'src/app/services/record.service';
import { MatDialogRef } from '@angular/material/dialog';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';
import { ClassifierInterface } from 'src/app/home/classifier/interfaces/classifier-interface';
import { ClassifierService } from 'src/app/home/classifier/services/classifier.service';
import { ClassifierCodeTypeList } from 'src/app/home/classifier/interfaces/classifier-code-type-list';

@Component({
  selector: 'app-create-application-s2272',
  templateUrl: './create-application-s2272.component.html',
  styleUrls: ['./create-application-s2272.component.scss'],
})
export class CreateApplicationS2272Component {
  public foodTypes: FoodTypeInterface[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public settlements: SettlementInterface[] | null;
  public branches: BranchInterface[];
  public municipalitySettlements: SettlementInterface[];
  public areaSettlements: SettlementInterface[];
  public correspondenceMunicipalitySettlements: SettlementInterface[];
  public correspondenceSettlements: SettlementInterface[] | null;
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public measuringUnits: NomenclatureInterface[];

  constructor(
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private settlementService: SettlementService,
    private recordService: RecordService,
    public dialogRef: MatDialogRef<CreateApplicationS2272Component>,
    private classifierService: ClassifierService
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
    serviceType: ['S2272'],
    branchIdentifier: ['176986657', Validators.required],

    vehicles: this.fb.array([
      this.fb.group({
        vehicleTypeCode: ['', Validators.required],
        brandModel: ['', Validators.required],
        registrationPlate: ['', Validators.required],
        vehicleOwnershipTypeCode: ['', Validators.required],
        load: ['', Validators.required],
        volume: [''],
        loadUnitCode: ['', Validators.required],
        volumeUnitCode: [''],
        foodTypes: [[], Validators.required],
        description: ['', Validators.required],
        transportationLicense: [false, Validators.required],
      }),
    ]),
  });

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllBranches();
    this.getAllSettlements();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();
    this.getMeasuringUnits();
    this.getClassifiers();
  }

  get vehicles() {
    return this.applicationForm?.controls['vehicles'] as FormArray;
  }

  addVehicle() {
    const vehicleForm = this.fb.group({
      vehicleTypeCode: ['', Validators.required],
      brandModel: ['', Validators.required],
      registrationPlate: ['', Validators.required],
      vehicleOwnershipTypeCode: ['', Validators.required],
      load: ['', Validators.required],
      volume: ['', Validators.required],
      loadUnitCode: ['', Validators.required],
      volumeUnitCode: ['', Validators.required],
      foodTypes: [[], Validators.required],
      description: ['', Validators.required],
      transportationLicense: [false, Validators.required],
    });

    this.vehicles.push(vehicleForm);
  }

  onToggleChange(data: { foodType: any; checked: boolean }, index: number) {
    const code = data.foodType.code;
    const isChecked = data.checked;
    const foodType = data.foodType;
    const currentFormValue = this.vehicles.value[index].foodTypes;
    if (!isChecked) {
      let index = currentFormValue.findIndex((el: any) => el.code === code);
      currentFormValue.splice(index, 1);
    } else {
      currentFormValue.push(foodType);
    }

    (this.vehicles.controls[index] as FormGroup)
      .get('foodTypes')
      ?.setValue(currentFormValue);

    console.log(this.vehicles);
  }

  removeVehicle(i: number) {
    this.vehicles.removeAt(i);
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

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
    });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
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

  private getClassifiers() {
    this.classifierService
      .getClassifier(ClassifierCodeTypeList.foodType)
      .subscribe((foodType: ClassifierInterface) => {
        console.log(foodType);
        this.foodTypes = foodType.subClassifiers;
      });
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

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

  closeDialog() {
    this.dialogRef.close();
  }

  registerApplication() {
    console.log(this.applicationForm.value);

    this.recordService
      .registerApplicationS2272(this.applicationForm.value)
      .subscribe((res) => {
        console.log('in response', res);

        this.dialogRef.close(res);
      });
  }
}
