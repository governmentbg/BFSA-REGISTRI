import { Component, Inject, ViewChild } from '@angular/core';
import { EditApplicationS1199Component } from '../edit-application-s1199/edit-application-s1199.component';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { MatStepper } from '@angular/material/stepper';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';
import { ApprovalOrderDateDialogComponent } from '../approval-order-date-dialog/approval-order-date-dialog.component';
import { DataDialogComponent } from '../data-dialog/data-dialog.component';

@Component({
  selector: 'app-edit-application-s2700',
  templateUrl: './edit-application-s2700.component.html',
  styleUrls: ['./edit-application-s2700.component.scss'],
})
export class EditApplicationS2700Component {
  @ViewChild(MatStepper) stepper: MatStepper;
  public applicationForm: FormGroup;
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];
  public finalActionErrors: String = '';

  constructor(
    public dialogRef: MatDialogRef<EditApplicationS1199Component>,
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
    this.getBranches();
    this.getRequestorAuthorTypes();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        ch83CertifiedPersons: this.fb.array([]),
      });
      if (this.data?.ch83CertifiedPersons?.length > 0) {
        this.fillCh83CertifiedPersons();
      }
      this.applicationForm.disable();
    }
  }

  fillCh83CertifiedPersons() {
    this.data?.ch83CertifiedPersons?.map((el: any, index: number) => {
      const ch83CertifiedPersonForm = this.fb.group({
        identifier: el.identifier,
        name: el.name,
        surname: el.surname,
        familyName: el.familyName,
      });
      this.ch83CertifiedPersons.push(ch83CertifiedPersonForm);
    });
  }

  private getBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  private getRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  closeDialog() {
    this.dialogRef.close();
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
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

  downloadOrder() {
    this.documentService
      .downloadOrderByRecordId(this.applicationForm.get('recordId')?.value)
      .subscribe((res: any) => {
        const contentType = res.headers.get('content-type');
        const fileName = res.headers.get('File-Name');
        this.downloadFile(res.body, contentType, fileName);
      });
  }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }

  get ch83CertifiedPersons() {
    return this.applicationForm?.get('ch83CertifiedPersons') as FormArray;
  }
}
