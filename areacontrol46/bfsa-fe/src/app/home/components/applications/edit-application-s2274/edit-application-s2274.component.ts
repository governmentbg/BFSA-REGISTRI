import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import * as dayjs from 'dayjs';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';
import { ApprovalOrderDateDialogComponent } from '../approval-order-date-dialog/approval-order-date-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';

@Component({
  selector: 'app-edit-application-s2274',
  templateUrl: './edit-application-s2274.component.html',
  styleUrls: ['./edit-application-s2274.component.scss'],
})
export class EditApplicationS2274Component {
  public applicationForm: FormGroup;
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';
  public pppManufacturersArray: {}[] = [];
  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS2274Component>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private readonly recordService: RecordService,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    if (this.data) {
      this.buildForm();
      this.applicationForm.disable();
    }
  }

  buildForm() {
    this.applicationForm = this.fb.group({
      recordId: [this.data?.recordId],
      permitHolderIdentifier: [this.data?.permitHolderIdentifier],
      permitHolderName: [this.data?.permitHolderName],
      permitHolderHeadquarter: [this.data?.permitHolderHeadquarter],
      permitHolderAddress: [this.data?.permitHolderAddress],
      permitHolderNameLat: [this.data?.permitHolderNameLat],
      permitHolderHeadquarterLat: [this.data?.permitHolderHeadquarterLat],
      permitHolderAddressLat: [this.data?.permitHolderAddressLat],
      permitHolderEmail: [this.data?.permitHolderEmail],
      pppName: [this.data?.pppName],
      pppCode: [this.data?.pppCode],
      pppFormulationTypeName: [this.data?.pppFormulationTypeName],
      pppManufacturers: this.fb.array([]),
      activeSubstances: this.fb.array([]),
      pppFunctions: [
        this.data?.pppFunctions?.map((el: any) => el.name).join(', '),
      ],
      pppActions: [this.data?.pppActions?.map((el: any) => el.name).join(', ')],
      pppCategoryName: [this.data?.pppCategoryName],
      pppActiveSubstanceTypes: [
        this.data?.pppActiveSubstanceTypes
          ?.map((el: any) => el.name)
          .join(', '),
      ],
      pppDescription: [this.data?.pppDescription],
      packages: this.fb.array([]),
      euReferenceProducts: this.fb.array([]),
      bgReferenceProducts: this.fb.array([]),
    });

    //map through set and fill formArray
    if (this.data.pppManufacturers) {
      Object.entries(this.data.pppManufacturers).map((el) => {
        const pppManufacturerForm = this.fb.group({
          pppManufacturerCyrilic: el[0],
          pppManufacturerLatin: el[1],
        });
        this.pppManufacturers.push(pppManufacturerForm);
      });
    }

    if (this.data.activeSubstances) {
      this.data.activeSubstances.map((el: any) => {
        const activeSubstanceForm = this.fb.group({
          activeSubstanceName: el.activeSubstanceName,
          manufacturer: el.manufacturer,
          manufacturerLat: el.manufacturerLat,
          manufacturerCountryName: el.manufacturerCountryName,
          substanceQuantity: el.substanceQuantity,
          substanceQuantityUnitName: el.substanceQuantityUnitName,
        });
        this.activeSubstances.push(activeSubstanceForm);
      });
    }

    if (this.data.packages) {
      this.data.packages.map((el: any) => {
        const packageForm = this.fb.group({
          type: el.type,
          material: el.material,
          size: el.size,
          description: el.description,
          quantities: this.fb.array(
            el.quantities.map((el: any) => {
              return this.fb.group({
                name: el.name,
                number: el.number,
              });
            })
          ),
        });
        this.packages.push(packageForm);
      });
    }

    if (this.data.referenceProducts) {
      this.data.referenceProducts.map((el: any) => {
        if (el.bgReferenceProduct) {
          this.bgReferenceProducts.push(
            this.fb.group({
              countryName: el.countryName,
              bgReferenceProduct: el.bgReferenceProduct,
              tradeName: el.tradeName,
              permitNumber: el.permitNumber,
              permitDate: this.formatDate(el.permitDate),
              permitValidUntilDate: this.formatDate(el.permitValidUntilDate),
              permitOwner: this.formatDate(el.permitOwner),
            })
          );
        } else {
          this.euReferenceProducts.push(
            this.fb.group({
              countryName: el.countryName,
              tradeName: el.tradeName,
              permitNumber: el.permitNumber,
              permitDate: this.formatDate(el.permitDate),
              permitValidUntilDate: this.formatDate(el.permitValidUntilDate),
              permitOwner: this.formatDate(el.permitOwner),
            })
          );
        }

        // this.referenceProducts.push(referenceProductForm);
      });
    }
  }

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
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

  get activeSubstances() {
    return this.applicationForm?.controls['activeSubstances'] as FormArray;
  }

  get pppManufacturers() {
    return this.applicationForm?.controls['pppManufacturers'] as FormArray;
  }

  get packages() {
    return this.applicationForm?.controls['packages'] as FormArray;
  }

  get bgReferenceProducts() {
    return this.applicationForm?.controls['bgReferenceProducts'] as FormArray;
  }

  get euReferenceProducts() {
    return this.applicationForm?.controls['euReferenceProducts'] as FormArray;
  }

  getProductQuantities(index: number): FormArray {
    const packageForm = (this.applicationForm.get('packages') as FormArray).at(
      index
    ) as FormGroup;
    return packageForm.get('quantities') as FormArray;
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
    const dialogRef = this.dialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }
}
