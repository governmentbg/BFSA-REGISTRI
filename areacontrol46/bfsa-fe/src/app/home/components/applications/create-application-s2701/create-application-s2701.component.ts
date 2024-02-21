import { Component, Inject, ViewChild } from '@angular/core';
import { FormArray, UntypedFormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApplicationsAddressTypeCodes } from '../intefaces/s2701-interface';
import { MatStepper } from '@angular/material/stepper';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-create-application-s2701',
  templateUrl: './create-application-s2701.component.html',
  styleUrls: ['./create-application-s2701.component.scss'],
})
export class CreateApplicationS2701Component {
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
  public facilityStatuses = Object.entries(recordStatus);
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS2701Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService
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
    branchIdentifier: ['176986657', Validators.required],
    applicantCorrespondenceAddress: this.fb.group({
      address: ['', Validators.required],
      addressTypeCode: [
        ApplicationsAddressTypeCodes.CORRESPONDENCE_ADDRESS_TYPE_CODE,
      ],
      settlementCode: ['', Validators.required],
      postCode: ['', Validators.required],
    }),

    educationalInstitution: ['test', Validators.required],
    educationalDocumentNumber: ['123', Validators.required],
    educationalDocumentType: ['DIPLOMA', Validators.required],
    educationalDocumentDate: [new Date(), Validators.required],
  });

  get applicantAddresses() {
    return this.applicationForm.controls['applicantAddresses'] as FormArray;
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

    this.applicationForm
      .get('requestorApplicantMatch')
      ?.valueChanges.subscribe((val) => {
        const applicantFullname = this.applicationForm.get('applicantFullName');
        const applicantIdentifier = this.applicationForm.get(
          'applicantIdentifier'
        );
        const applicantEmail = this.applicationForm.get('applicantEmail');
        if (val) {
          applicantFullname?.setValue(
            this.applicationForm.get('requestorFullName')?.value
          );
          applicantIdentifier?.setValue(
            this.applicationForm.get('requestorIdentifier')?.value
          );
          applicantEmail?.setValue(
            this.applicationForm.get('requestorEmail')?.value
          );
          applicantFullname?.disable();
          applicantIdentifier?.disable();
          applicantEmail?.disable();
        } else {
          applicantFullname?.setValue(null);
          applicantIdentifier?.setValue(null);
          applicantEmail?.setValue(null);

          applicantFullname?.enable();
          applicantIdentifier?.enable();
          applicantEmail?.enable();
        }
      });

    this.applicationForm
      .get('requestorEmail')
      ?.valueChanges.subscribe((val) => {
        if (
          this.applicationForm.get('applicantEmail')?.value === val &&
          !this.applicationForm.get('requestorApplicantMatch')?.value
        ) {
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
        if (
          this.applicationForm.get('requestorEmail')?.value === val &&
          !this.applicationForm.get('requestorApplicantMatch')?.value
        ) {
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
  }

  closeDialog() {
    this.dialogRef.close();
  }

  addDays(date: Date, days: number) {
    let result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  fixDate() {
    this.applicationForm
      .get('educationalDocumentDate')
      ?.setValue(
        this.addDays(
          this.applicationForm.get('educationalDocumentDate')?.value,
          1
        )
          .toISOString()
          .slice(0, 10)
      );
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

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
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

  registerApplication() {
    this.fixDate();
    console.log(this.applicationForm.value);

    this.recordService
      .registerApplicationS2701(this.applicationForm.getRawValue())
      .subscribe((res) => {
        console.log(res);
        this.recordId = res.recordId;
        console.log('in response', res);

        this.stepper.next();
        //this.dialogRef.close(res);
      });
    //on error don't close
  }
}
