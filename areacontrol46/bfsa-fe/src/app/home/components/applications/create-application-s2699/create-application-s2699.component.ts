import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, Validators, FormArray } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { CreateApplicationS1590Component } from '../create-application-s1590/create-application-s1590.component';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-create-application-s2699',
  templateUrl: './create-application-s2699.component.html',
  styleUrls: ['./create-application-s2699.component.scss'],
})
export class CreateApplicationS2699Component {
  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS1590Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService
  ) {}

  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[] | null;
  public correspondenceMunicipalitySettlements: SettlementInterface[];
  public correspondenceSettlements: SettlementInterface[] | null;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];

  public applicationForm = this.fb.group({
    branchIdentifier: ['176986657'],
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

    seedTreatmentFacilityAddress: this.fb.group({
      address: ['asdasd', Validators.required],
      settlementCode: ['', Validators.required],
    }),

    ch83CertifiedPerson: this.fb.group({
      name: ['asdasd', Validators.required],
      surname: ['gasfaga', Validators.required],
      familyName: ['sdgsdfsdf', Validators.required],
      identifier: ['', Validators.required],
      certificateByClause83: [false, Validators.requiredTrue],
    }),

    ch83CertifiedPersons: this.fb.array([
      this.fb.group({
        name: ['', Validators.required],
        surname: ['', Validators.required],
        familyName: ['', Validators.required],
        identifier: ['', Validators.required],
        certificateByClause83: [false, Validators.requiredTrue],
      }),
    ]),
    //   name: ['asdasd', Validators.required],
    //   surname: ['', Validators.required],
    //   familyName: ['', Validators.required],
    //   identifier: ['', Validators.required],
    // }),
  });

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
  }

  closeDialog() {
    this.dialogRef.close();
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

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  get ch83CertifiedPersons() {
    return this.applicationForm?.controls['ch83CertifiedPersons'] as FormArray;
  }

  addFumigationPerformingPerson() {
    const fumigationPersonForm = this.fb.group({
      name: ['', Validators.required],
      surname: ['', Validators.required],
      familyName: ['', Validators.required],
      identifier: ['', Validators.required],
      fumigationCertificate: [false, Validators.required],
      certificateByClause83: [false, Validators.required],
    });

    this.ch83CertifiedPersons.push(fumigationPersonForm);
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
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

  removeTreatmentSeedPerson(i: number) {
    this.ch83CertifiedPersons.removeAt(i);
  }

  registerApplication() {
    console.log(this.applicationForm.value);
    this.recordService
      .registerApplicationS2699(this.applicationForm.value)
      .subscribe((res) => {
        console.log('in response', res);
        this.dialogRef.close(res);
      });
    //on error don't close
  }
}
