import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import * as dayjs from 'dayjs';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import * as saveAs from 'file-saver';
import { DocumentService } from 'src/app/services/document.service';

@Component({
  selector: 'app-edit-application-s2695',
  templateUrl: './edit-application-s2695.component.html',
  styleUrls: ['./edit-application-s2695.component.scss'],
})
export class EditApplicationS2695Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );

  get plantProtectionProducts() {
    return this.applicationForm?.controls[
      'plantProtectionProducts'
    ] as FormArray;
  }

  get subAgricultures() {
    return this.applicationForm?.controls['subAgricultures'] as FormArray;
  }

  get distantNeighborSettlements() {
    return this.applicationForm
      ?.get('field')
      ?.get('distantNeighborSettlements') as FormArray;
  }

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS2695Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    private readonly recordService: RecordService,
    private readonly documentService: DocumentService,
    public refusalDialog: MatDialog
  ) {}

  ngOnInit() {
    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        aerialSprayStartDate: [
          this.formatDate(this.data?.aerialSprayStartDate),
        ],
        aerialSprayEndDate: [this.formatDate(this.data?.aerialSprayEndDate)],
        aerialSprayAgriculturalGroupName: [
          this.data?.aerialSprayAgriculturalGroupName,
        ],
        subAgricultures: [
          this.data.subAgricultures?.map((el: any, index: number) => {
            if (index !== 0) {
              return ' ' + el.name;
            }
            return el.name;
          }),
        ],
        plantProtectionProducts: this.fb.array([]),
        phenophaseCultureName: [this.data?.phenophaseCultureName],
        field: this.fb.group({
          fullAddress: [this.data.field?.treatmentAddress?.fullAddress],
          treatmentDate: [this.formatDate(this.data.field?.treatmentDate)],
          treatmentStartHour: [
            this.formatHours(this.data.field?.treatmentStartHour),
          ],
          treatmentEndHour: [
            this.formatHours(this.data.field?.treatmentEndHour),
          ],
          land: [this.data.field?.treatmentAddress?.land],
          treatmentArea: [this.data.field?.treatmentArea],
          treatmentDistance: [this.data.field?.treatmentDistance],

          distantNeighborSettlements: this.fb.array([]),
        }),
        aviationOperatorName: [this.data?.aviationOperatorName],
        aviationOperatorIdentifier: [this.data?.aviationOperatorIdentifier],
        aviationCh64CertNumber: [this.data?.aviationCh64CertNumber],
        aviationCh64CertDate: [
          this.formatDate(this.data?.aviationCh64CertDate),
        ],

        ch83CertifiedPerson: this.fb.group({
          fullName: [this.data?.ch83CertifiedPerson?.fullName],
          identifier: [this.data?.ch83CertifiedPerson?.identifier],
        }),
        workLand: [this.data?.workLand],
        worksite: [this.data?.worksite],
      });
      if (this.data.plantProtectionProducts?.length) {
        this.fillPpp();
      }
      if (this.data.field?.distantNeighborSettlements.length) {
        this.fillDistantNeighborSettlements();
      }
      this.applicationForm.disable();
    }
  }

  fillPpp() {
    this.data?.plantProtectionProducts.map((el: any, index: number) => {
      let pppPests = el.pppPests;
      const pppForm = this.fb.group({
        pppFunctionName: el.pppFunctionName,
        pppName: el.pppName,
        pppAerialSpray: true,
        pppPurchase: el.pppPurchase,
        pppDose: el.pppDose,
        pppUnitName: el.pppUnitName,
        pppQuarantinePeriod: el.pppQuarantinePeriod,
        pppPests: this.fb.array(
          pppPests.map((el: any, index: number) => {
            return this.fb.group({
              pestGroupName: el.pestGroupName,
              pest: el.pest,
            });
          })
        ),
      });
      this.plantProtectionProducts.push(pppForm);
    });
  }

  getNestedPestsArrayAtIndex(productIndex: number): FormArray {
    const product = (
      this.applicationForm.get('plantProtectionProducts') as FormArray
    ).at(productIndex) as FormGroup;
    return product.get('pppPests') as FormArray;
  }

  fillDistantNeighborSettlements() {
    this.data?.field?.distantNeighborSettlements.map(
      (el: any, index: number) => {
        const distantNeighborSettlementForm = this.fb.group({
          treatmentSettlements: el.treatmentSettlements,
          treatmentDistances: el.treatmentDistances,
        });

        this.distantNeighborSettlements.push(distantNeighborSettlementForm);
      }
    );
  }

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  formatHours(date: string) {
    return dayjs(date).format('HH:mm');
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
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
}
