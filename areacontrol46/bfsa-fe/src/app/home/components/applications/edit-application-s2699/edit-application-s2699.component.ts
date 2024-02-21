import { Component, Inject } from '@angular/core';
import { FormGroup, UntypedFormBuilder, FormArray } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { BranchInterface } from 'src/app/home/contractor/interfaces/branch-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { AddressService } from 'src/app/services/address.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { CreateApplicationS3180Component } from '../create-application-s3180/create-application-s3180.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';
import { ApprovalOrderDateDialogComponent } from '../approval-order-date-dialog/approval-order-date-dialog.component';
import { DataDialogComponent } from '../data-dialog/data-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-edit-application-s2699',
  templateUrl: './edit-application-s2699.component.html',
  styleUrls: ['./edit-application-s2699.component.scss'],
})
export class EditApplicationS2699Component {
  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[] | null;
  public correspondenceMunicipalitySettlements: SettlementInterface[];
  public correspondenceSettlements: SettlementInterface[] | null;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public applicationForm: FormGroup;
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS3180Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly addressService: AddressService,
    public dialog: MatDialog,
    public refusalDialog: MatDialog,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        branchIdentifier: [this.data.branchIdentifier],
        seedTreatmentFacilityAddress: this.fb.group({
          fullAddress: [this.data.seedTreatmentFacilityAddress?.fullAddress],
        }),
        ch83CertifiedPerson: this.fb.group({
          fullName: [this.data.ch83CertifiedPerson?.fullName],
          identifier: [this.data.ch83CertifiedPerson?.identifier],
        }),
        ch83CertifiedPersons: this.fb.array([]),
      });
      if (this.data?.ch83CertifiedPersons?.length) {
        this.fillCh83CertifiedPersons();
      }
      this.applicationForm.disable();
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  get ch83CertifiedPersons() {
    return this.applicationForm?.controls['ch83CertifiedPersons'] as FormArray;
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

  fillCh83CertifiedPersons() {
    this.data?.ch83CertifiedPersons.map((el: any, index: number) => {
      const treatmentSeedPersonForm = this.fb.group({
        fullName: el.fullName,
        identifier: el.identifier,
      });
      this.ch83CertifiedPersons.push(treatmentSeedPersonForm);
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
                .open(DataDialogComponent, {
                  height: '300px',
                  width: '400px',
                  data: res,
                })
                .afterClosed();
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

  downloadOrder() {
    this.documentService
      .downloadOrderByRecordId(this.applicationForm.get('recordId')?.value)
      .subscribe((res: any) => {
        const contentType = res.headers.get('content-type');
        const fileName = res.headers.get('File-Name');
        this.downloadFile(res.body, contentType, fileName);
      });
  }
}
