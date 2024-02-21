import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { BranchService } from 'src/app/services/branch.service';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { EditApplicationS503Component } from '../edit-application-s503/edit-application-s503.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import * as saveAs from 'file-saver';

@Component({
  selector: 'app-edit-application-s2711',
  templateUrl: './edit-application-s2711.component.html',
  styleUrls: ['./edit-application-s2711.component.scss'],
})
export class EditApplicationS2711Component {
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public activityTypes: NomenclatureInterface[];
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';

  constructor(
    public dialogRef: MatDialogRef<EditApplicationS503Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public refusalDialog: MatDialog,
    private fb: UntypedFormBuilder,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    private recordService: RecordService,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getActivityTypes();
    this.getAllBranches();
    this.getAllRequestorAuthorTypes();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        fullAddress: [this.data.fullAddress],
        facilityTypeName: [this.data.facilityTypeName],
        facilityAddress: this.fb.group({
          identifier: [this.data.facilityAddress?.identifier],
          fullAddress: [this.data.facilityAddress?.fullAddress],
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

  get ch83CertifiedPersons() {
    return this.applicationForm?.controls['ch83CertifiedPersons'] as FormArray;
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
      });
  }

  fillCh83CertifiedPersons() {
    this.data?.ch83CertifiedPersons.map((el: any, index: number) => {
      const ch83CertifiedPersonForm = this.fb.group({
        fullName: el.fullName,
        identifier: el.identifier,
      });
      this.ch83CertifiedPersons.push(ch83CertifiedPersonForm);
    });
  }

  closeDialog() {
    this.dialogRef.close();
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
              if (res) {
                this.documentService.downloadRefusalDocumentByServiceType(
                  this.data.serviceType.toLowerCase(),
                  this.applicationForm.get('recordId')?.value
                );
              }
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

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
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
}
