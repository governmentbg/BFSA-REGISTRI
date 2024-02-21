import { Component, Inject, ViewChild } from '@angular/core';
import { UntypedFormBuilder, Validators, FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatStepper } from '@angular/material/stepper';
import { recordStatus } from 'src/app/enums/recordStatus';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { CreateApplicationS2701Component } from '../create-application-s2701/create-application-s2701.component';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { CountryService } from 'src/app/home/inspection/services/country.service';

@Component({
  selector: 'app-create-application-s2702',
  templateUrl: './create-application-s2702.component.html',
  styleUrls: ['./create-application-s2702.component.scss'],
})
export class CreateApplicationS2702Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public recordId: string;
  public selectedFile: File | null = null;
  public areEmailsEqual: boolean;
  public activityGroupParents: any[];
  public branches: any[];
  public animalSpecies: any[];
  public remarks: any[];
  public selectedRelatedActivityCategories: any[];
  public relatedActivityCategories: any;
  public associatedActivityCategories: any;
  public subActivityGroups: any;
  public pictograms: any[];
  public requestorAuthorTypes: NomenclatureInterface[];
  // TODO to set a type on all ^
  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[] | null;
  public contractorAreaSettlements: SettlementInterface[] | null;
  public contractorMunicipalitySettlements: SettlementInterface[] | null;
  public contractorSettlements: SettlementInterface[] | null;
  public facilityStatuses = Object.entries(recordStatus);
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];
  public measuringUnits: NomenclatureInterface[];
  public activityTypes: NomenclatureInterface[];
  public contractorActivityTypes: NomenclatureInterface[];
  public materialEndUses: NomenclatureInterface[];
  public euCountries: any[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS2701Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly countryService: CountryService
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
    requestorPhone: ['456-678-999', [Validators.required]],
    requestorAuthorTypeCode: ['01301', [Validators.required]],
    requestorPowerAttorneyNumber: [''],
    requestorPowerAttorneyNotary: [''],
    requestorPowerAttorneyDate: [''],
    requestorPowerAttorneyUntilDate: [''],
    requestorApplicantMatch: [false, Validators.required],
    applicantFullName: ['test', [Validators.required]],
    applicantIdentifier: [
      Math.floor(Math.random() * 10000000000),
      [Validators.required],
    ],
    applicantEmail: [
      `test-${Math.random().toString(16).slice(2, 8)}@mail.com`,
      Validators.required,
    ],
    applicantPhone: ['123-123-123', Validators.required],
    applicantCorrespondenceAddress: this.fb.group({
      address: ['', Validators.required],
      addressTypeCode: [
        ApplicationsAddressTypeCodes.CORRESPONDENCE_ADDRESS_TYPE_CODE,
      ],
      settlementCode: ['', Validators.required],
      postCode: ['', Validators.required],
    }),
    branchIdentifier: ['176986657', Validators.required],

    activityResponsiblePersons: this.fb.array([
      this.fb.group({
        name: ['', Validators.required],
        surname: ['', Validators.required],
        familyName: ['', Validators.required],
        identifier: ['', Validators.required],
        phone: ['', Validators.required],
        email: ['', Validators.required],
        degree: ['', Validators.required],
      }),
    ]),

    materialType: ['', Validators.required],
    materialName: ['', Validators.required],
    materialTotalAmount: ['', Validators.required],
    materialMeasuringUnitCode: ['', Validators.required],
    materialExportCountryCode: [''],
    materialMovements: this.fb.array([
      this.fb.group({
        materialQuantity: ['', Validators.required],
      }),
    ]),
    materialOriginCountryCode: ['', Validators.required],
    materialPackagingCondition: ['', Validators.required],
    supplier: this.fb.group({
      contractorActivityTypeCode: ['', Validators.required],
      name: [''],
      surname: [''],
      familyName: [''],
      identifier: ['', Validators.required],
      fullName: [''],
      entityType: ['LEGAL', Validators.required],
      phone: ['', Validators.required],
      email: ['', Validators.required],
    }),
    supplierAddress: this.fb.group({
      countryCode: ['', Validators.required],
      settlementName: [''],
      settlementCode: [null],
      address: ['', Validators.required],
    }),
    quarantineStationName: ['', Validators.required],
    quarantineStationDescription: ['', Validators.required],
    quarantineStationAddress: this.fb.group({
      settlementCode: ['', Validators.required],
      address: ['', Validators.required],
    }),
    quarantineStationPerson: this.fb.group({
      name: ['', Validators.required],
      surname: [''],
      familyName: [''],
      identifier: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', Validators.required],
      degree: ['', Validators.required],
    }),
    quarantineStationMaterialStorageMeasure: ['', Validators.required],
    requestedActivitySummary: [''],
    firstEntryDate: ['', Validators.required],
    expectedCompletionDate: ['', Validators.required],
    materialEndUseCode: ['', Validators.required],
    materialDestructionMethod: ['', Validators.required],
    materialSafeMeasure: ['', Validators.required],
    description: ['', Validators.required],
  });

  get applicantAddresses() {
    return this.applicationForm.controls['applicantAddresses'] as FormArray;
  }

  public get isPhysicalPersonForm() {
    return (
      this.applicationForm.get('supplier')?.get('entityType')?.value ===
      'PHYSICAL'
    );
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

  onChangeContractorZone(id: string) {
    this.settlementService.getMunicipalitiesByCode(id).subscribe((res) => {
      this.contractorMunicipalitySettlements = res;
      this.settlements = null;
      this.applicationForm
        .get('supplierAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('supplierAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  onChangeContractorMunicipalitySettlement(id: string) {
    this.settlementService.getRegionsByCode(id).subscribe((res) => {
      this.contractorSettlements = res;
      this.applicationForm
        .get('supplierAddress')
        ?.get('settlementCode')
        ?.setValue(null);
      this.applicationForm
        .get('supplierAddress')
        ?.get('settlementCode')
        ?.markAsTouched();
    });
  }

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getMeasuringUnits();
    this.getActivityTypes();
    this.getContractorActivityTypes();
    this.getMaterialEndUses();
    this.getEuropeanCountries();

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

    this.applicationForm
      .get('supplierAddress')
      ?.get('countryCode')
      ?.valueChanges.subscribe((val) => {
        if (val) {
          this.applicationForm
            ?.get('supplierAddress')
            ?.get('settlementName')
            ?.setValue(null);
          this.applicationForm
            ?.get('supplierAddress')
            ?.get('settlementCode')
            ?.setValue(null);
        }
      });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
      this.contractorAreaSettlements = response;
    });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
      });
  }

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
      });
  }

  private getContractorActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('03200')
      .subscribe((response: NomenclatureInterface[]) => {
        this.contractorActivityTypes = response;
      });
  }

  private getMaterialEndUses() {
    this.nomenclatureService
      .getNomenclatureByParentCode('03300')
      .subscribe((response: NomenclatureInterface[]) => {
        this.materialEndUses = response;
      });
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

  private getEuropeanCountries() {
    this.countryService.getEuropeanCountries().subscribe((res: any) => {
      this.euCountries = res;
    });
  }

  removeActivityResponsiblePerson(i: number) {
    this.activityResponsiblePersons.removeAt(i);
  }

  removeMaterialMovement(i: number) {
    this.materialMovements.removeAt(i);
  }

  get activityResponsiblePersons() {
    return this.applicationForm?.controls[
      'activityResponsiblePersons'
    ] as FormArray;
  }

  get materialMovements() {
    return this.applicationForm?.get('materialMovements') as FormArray;
  }

  get materialQuantitySum() {
    let sum = 0;
    this.materialMovements.value.map(
      (el: any) => (sum += Number(el.materialQuantity))
    );
    return sum;
  }

  onFileSelected(fileInput: any) {
    this.selectedFile = fileInput.files[0];
    this.uploadFile();
  }

  uploadFile() {
    if (!this.selectedFile) {
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    console.log(formData);

    this.recordService
      .attachCertificateImageS2701(this.recordId, formData)
      .subscribe((res) => console.log(res));
  }

  addActivityResponsiblePerson() {
    const activityResponsiblePersonForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      familyName: ['', Validators.required],
      identifier: ['', Validators.required],
      phone: ['', Validators.required],
      email: ['', Validators.required],
      degree: ['', Validators.required],
    });

    this.activityResponsiblePersons.push(activityResponsiblePersonForm);
    console.log(this.activityResponsiblePersons);
  }

  addMaterialMovement() {
    const materialMovementForm = this.fb.group({
      materialQuantity: ['', Validators.required],
    });

    this.materialMovements.push(materialMovementForm);
    console.log(this.materialMovements);
  }

  registerApplication() {
    let dto = this.applicationForm.value;
    const materialMovements: any[] = [];
    this.materialMovements.value.map((el: any) =>
      materialMovements.push(el.materialQuantity)
    );
    dto.materialMovements = materialMovements;
    dto.materialTotalAmount = this.materialQuantitySum;
    console.log(dto);
    this.recordService.registerApplicationS2702(dto).subscribe((res) => {
      console.log(res);
      this.recordId = res.recordId;
      console.log('in response', res);

      this.stepper.next();
      this.dialogRef.close(res);
    });
    //on error don't close
  }
}
