import { Component, Inject, ViewChild } from '@angular/core';
import {
  FormArray,
  FormGroup,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { CountryService } from 'src/app/home/inspection/services/country.service';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { CreateApplicationS2701Component } from '../create-application-s2701/create-application-s2701.component';
import { MatStepper } from '@angular/material/stepper';
import { recordStatus } from 'src/app/enums/recordStatus';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { DocumentService } from 'src/app/services/document.service';
import * as saveAs from 'file-saver';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';

@Component({
  selector: 'app-edit-application-s2702',
  templateUrl: './edit-application-s2702.component.html',
  styleUrls: ['./edit-application-s2702.component.scss'],
})
export class EditApplicationS2702Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public recordId: string;
  public selectedFile: File | null = null;
  public areEmailsEqual: boolean;
  public activityGroupParents: any[];
  public branches: any[];
  public animalSpecies: any[];
  public remarks: any[];
  public foodTypes: any[];
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
  public applicationForm: FormGroup;
  public datePipe: any;
  public correspondenceMunicipalitySettlements: SettlementInterface[];
  public correspondenceSettlements: SettlementInterface[] | null;
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS2701Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly countryService: CountryService,
    public dialog: MatDialog,
    private documentService: DocumentService,
    public refusalDialog: MatDialog
  ) {}

  get applicantAddresses() {
    return this.applicationForm.controls['applicantAddresses'] as FormArray;
  }

  public get isPhysicalPersonForm() {
    return (
      this.applicationForm.get('supplier')?.get('entityType')?.value ===
      'PHYSICAL'
    );
  }

  get activityResponsiblePersons() {
    return this.applicationForm?.controls[
      'activityResponsiblePersons'
    ] as FormArray;
  }

  get materialQuantitySum() {
    let sum = 0;
    this.materialMovements.value.map(
      (el: any) => (sum += Number(el.materialQuantity))
    );
    return sum;
  }

  get materialMovements() {
    return this.applicationForm?.get('materialMovements') as FormArray;
  }

  ngOnInit() {
    this.buildForm();
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getMeasuringUnits();
    this.getActivityTypes();
    this.getContractorActivityTypes();
    this.getMaterialEndUses();
    this.getEuropeanCountries();
  }

  buildForm() {
    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data?.recordId],
        activityResponsiblePersons: this.fb.array([]),
        materialType: [this.data.materialType],
        materialName: [this.data.materialName],
        materialTotalAmount: [this.data.materialTotalAmount],
        materialMeasuringUnitName: [this.data.materialMeasuringUnitName],
        materialExportCountryCode: [this.data.materialExportCountryCode],
        materialMovements: this.fb.array([]),
        materialOriginCountryCode: [this.data.materialOriginCountryCode],
        materialPackagingCondition: [this.data.materialPackagingCondition],
        supplier: this.fb.group({
          contractorActivityTypeCode: [
            this.data.supplier?.contractorActivityTypeCode,
          ],
          identifier: [this.data.supplier?.identifier],
          fullName: [this.data.supplier?.fullName],
          entityType: [this.data.supplier?.entityType],
          phone: [this.data.supplier?.phone],
          email: [this.data.supplier?.email],
        }),
        supplierAddress: this.fb.group({
          countryCode: [this.data.supplierAddress?.countryCode],
          settlementName: [this.data.supplierAddress?.settlementName],
          //settlementCode: [this.data.supplierAddress.settlementCode],
          fullAddress: [this.data.supplierAddress?.fullAddress],
        }),
        quarantineStationName: [this.data.quarantineStationName],
        quarantineStationDescription: [this.data.quarantineStationDescription],
        quarantineStationAddress: this.fb.group({
          fullAddress: [this.data.quarantineStationAddress?.fullAddress],
        }),
        quarantineStationPerson: this.fb.group({
          fullName: [this.data.quarantineStationPerson?.fullName],
          identifier: [this.data.quarantineStationPerson?.identifier],
          phone: [this.data.quarantineStationPerson?.phone],
          email: [this.data.quarantineStationPerson?.email],
          degree: [this.data.quarantineStationPerson?.degree],
        }),
        quarantineStationMaterialStorageMeasure: [
          this.data.quarantineStationMaterialStorageMeasure,
        ],
        requestedActivitySummary: [this.data.requestedActivitySummary],
        firstEntryDate: [this.data.firstEntryDate],
        expectedCompletionDate: [this.data.expectedCompletionDate],
        materialEndUseCode: [this.data.materialEndUseCode],
        materialDestructionMethod: [this.data.materialDestructionMethod],
        materialSafeMeasure: [this.data.materialSafeMeasure],
        description: [this.data.description],
      });
      if (this.data?.materialMovements?.length) {
        this.fillMaterialMovements();
      }
      if (this.data?.activityResponsiblePersons?.length) {
        this.fillActivityResponsiblePersons();
      }
      //this.recordStatus = this.data?.recordStatus;
      this.applicationForm.disable();
    }
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

  fillMaterialMovements() {
    this.data?.materialMovements.map((el: any, index: number) => {
      const materialMovementForm = this.fb.group({
        materialQuantity: el,
      });
      this.materialMovements.push(materialMovementForm);
    });
  }

  fillActivityResponsiblePersons() {
    this.data?.activityResponsiblePersons.map((el: any, index: number) => {
      const activityResponsiblePersonForm = this.fb.group({
        fullName: el.fullName,
        identifier: el.identifier,
        phone: el.phone,
        email: el.email,
        degree: el.degree,
      });
      this.activityResponsiblePersons.push(activityResponsiblePersonForm);
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
      .get('expectedCompletionDate')
      ?.setValue(
        this.addDays(
          this.applicationForm.get('expectedCompletionDate')?.value,
          1
        )
          .toISOString()
          .slice(0, 10)
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

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
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
              const dialogRef = this.dialog
                .open(DataAndDownloadDialogComponent, {
                  height: '300px',
                  width: '400px',
                  data: res,
                })
                .afterClosed()
                .subscribe((res) => {
                  if (res) {
                    this.documentService
                      .downloadDocumentByRecordId(
                        this.applicationForm.get('recordId')?.value
                      )
                      .subscribe((res: any) => {
                        const contentType = res.headers.get('content-type');
                        const fileName = res.headers.get('File-Name');
                        this.downloadFile(res.body, contentType, fileName);
                        this.dialogRef.close(res);
                      });
                  }
                });
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

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }
}
