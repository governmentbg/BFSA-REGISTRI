import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import { FormGroup, UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { DocumentService } from 'src/app/services/document.service';
import * as saveAs from 'file-saver';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { DatePipe } from '@angular/common';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
@Component({
  selector: 'app-edit-contragent',
  templateUrl: './edit-application-s2701.component.html',
  styleUrls: ['./edit-application-s2701.component.scss'],
})
export class EditApplicationS2701Component implements OnInit, AfterViewInit {
  public applicationForm: FormGroup;

  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public recordStatus: string = '';
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
  public settlements: SettlementInterface[];
  public facilityStatuses = Object.entries(recordStatus);
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];
  public updatedApplicationState: boolean = false;
  public finalActionErrors: String = '';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS2701Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    public dialog: MatDialog,
    private readonly branchService: BranchService,
    private readonly documentService: DocumentService,
    private readonly cdr: ChangeDetectorRef,
    private datePipe: DatePipe,
    public refusalDialog: MatDialog
  ) {}

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

  ngOnInit() {
    if (this.data) {
      this.data.educationalDocumentDate = this.datePipe.transform(
        this.data.educationalDocumentDate,
        'dd-MM-yyyy'
      );

      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        educationalInstitution: [this.data.educationalInstitution],
        educationalDocumentNumber: [this.data.educationalDocumentNumber],
        educationalDocumentType: [this.data.educationalDocumentType],
        educationalDocumentDate: [this.data.educationalDocumentDate],
      });
      this.applicationForm.disable();
    }

    this.recordStatus = this.data?.recordStatus;
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();

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
      .get('educationalDocumentDate')
      ?.valueChanges.subscribe((val) => {
        if (val) {
          this.fixDate();
        }
      });
  }

  ngAfterViewInit() {
    if (this.data.approvalDocumentStatus !== 'ENTERED' && !this.isExpert) {
      this.applicationForm.disable();
      this.cdr.detectChanges();
    }
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
          .slice(0, 10),
        { emitEvent: false }
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

  public openCorrectionDialog() {
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
