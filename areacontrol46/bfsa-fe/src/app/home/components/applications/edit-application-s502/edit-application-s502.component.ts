import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MatDialog,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { Nomenclature } from 'src/app/models/nomenclature';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { saveAs } from 'file-saver';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { ApplicationService } from 'src/app/services/application.service';
import { BranchService } from 'src/app/services/branch.service';
import { PaymentConfirmDialogComponent } from '../payment-confirm-dialog/payment-confirm-dialog.component';
import { ApprovalOrderDateDialogComponent } from '../approval-order-date-dialog/approval-order-date-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';

@Component({
  selector: 'app-edit-application-s502',
  templateUrl: './edit-application-s502.component.html',
  styleUrls: ['./edit-application-s502.component.scss'],
})
export class EditApplicationS502Component {
  public applicationForm: FormGroup;
  public areaSettlements: SettlementInterface[];
  public requestorAuthorTypes: Nomenclature[];
  public branches: BranchInterface[];
  public finalActionErrors: String = '';
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public plantGroupTypes: Nomenclature[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS502Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public refusalDialog: MatDialog,
    private readonly documentService: DocumentService,
    private nomenclatureService: NomenclatureService,
    private readonly recordService: RecordService,
    private readonly applicationService: ApplicationService,
    private readonly branchService: BranchService
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getPlantGroupTypes();
    this.getAllBranches();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        testingAddresses: this.fb.array([]),
        plantGroupTypeCodes: [this.data.plantGroupTypeCodes],
        pppTestingPersons: this.fb.array([]),
        easedFacilities: [this.data.easedFacilities?.join(',')],
        maintenanceEquipments: this.fb.array([]),
        testMethodologies: this.fb.array([]),
        researchPlansDescription: [this.data.researchPlansDescription],
        archivingDocDescription: [this.data.archivingDocDescription],
      });
      if (this.data?.testingAddresses?.length) {
        this.fillTestingAddresses();
      }
      if (this.data?.pppTestingPersons?.length) {
        this.fillPppTestingPersons();
      }
      if (this.data.maintenanceEquipments?.length) {
        this.fillMaintenanceEquipments();
      }
      if (this.data.testMethodologies?.length) {
        this.fillTestMethodologies();
      }
      this.applicationForm.disable();
    }
  }

  private getPlantGroupTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('05200')
      .subscribe((res) => {
        this.plantGroupTypes = res;
      });
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  fillTestingAddresses() {
    this.data?.testingAddresses.map((el: any, index: number) => {
      const testingAddressForm = this.fb.group({
        fullAddress: el.fullAddress,
        settlementName: el.settlementName,
        land: el.land,
        plotNumber: el.plotNumber,
      });
      this.testingAddresses.push(testingAddressForm);
    });
  }

  fillPppTestingPersons() {
    this.data?.pppTestingPersons.map((el: any, index: number) => {
      const pppTestingPersonForm = this.fb.group({
        fullName: el.fullName,
        identifier: el.identifier,
        description: el.description,
      });
      this.pppTestingPersons.push(pppTestingPersonForm);
    });
  }

  fillMaintenanceEquipments() {
    this.data?.maintenanceEquipments.map((el: any, index: number) => {
      const maintenanceEquipmentForm = this.fb.group({
        equipmentSubTypeName: el.equipmentSubTypeName,
        equipmentTypeName: el.equipmentTypeName,
        equipmentName: el.equipmentName,
        description: el.description,
      });
      this.maintenanceEquipments.push(maintenanceEquipmentForm);
    });
  }

  fillTestMethodologies() {
    this.data?.testMethodologies.map((el: any, index: number) => {
      const testMethodologyForm = this.fb.group({
        typeName: el.typeName,
        description: el.description,
      });
      this.testMethodologies.push(testMethodologyForm);
    });
  }

  get testingAddresses() {
    return this.applicationForm?.controls['testingAddresses'] as FormArray;
  }

  get pppTestingPersons() {
    return this.applicationForm?.controls['pppTestingPersons'] as FormArray;
  }

  get easedFacilities() {
    return this.applicationForm?.controls['easedFacilities'] as FormArray;
  }

  get maintenanceEquipments() {
    return this.applicationForm?.controls['maintenanceEquipments'] as FormArray;
  }

  get testMethodologies() {
    return this.applicationForm?.controls['testMethodologies'] as FormArray;
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
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

  public openApprovalDialog() {
    const dialogRef = this.dialog.open(ApprovalOrderDateDialogComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const body = {
          recordId: this.applicationForm.get('recordId')?.value,
          orderNumber: result.orderNumber,
          orderDate: result.orderDate,
        };
        this.recordService
          .approveRegistrationBody(this.data.serviceType.toLowerCase(), body)
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

  downloadOrder() {
    this.documentService
      .downloadOrderByRecordId(this.applicationForm.get('recordId')?.value)
      .subscribe((res: any) => {
        const contentType = res.headers.get('content-type');
        const fileName = res.headers.get('File-Name');
        this.downloadFile(res.body, contentType, fileName);
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

  openPaymentConfirmationDialog() {
    const dialogRef = this.dialog.open(PaymentConfirmDialogComponent, {
      width: '40vw',
      autoFocus: false,
    });
    dialogRef.afterClosed().subscribe((recordPrice: number) => {
      if (recordPrice) {
        this.applicationService
          .sendForPayment(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            recordPrice
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

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }
}
