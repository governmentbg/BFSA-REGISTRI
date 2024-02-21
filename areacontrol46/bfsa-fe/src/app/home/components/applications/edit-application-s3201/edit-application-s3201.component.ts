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
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { saveAs } from 'file-saver';
import { DocumentService } from 'src/app/services/document.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';

@Component({
  selector: 'app-edit-application-s3201',
  templateUrl: './edit-application-s3201.component.html',
  styleUrls: ['./edit-application-s3201.component.scss'],
})
export class EditApplicationS3201Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public requestorAuthorTypes: NomenclatureInterface[];
  public branches: BranchInterface[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS3201Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    private readonly recordService: RecordService,
    public refusalDialog: MatDialog,
    private nomenclatureService: NomenclatureService,
    private branchService: BranchService,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllBranches();
    if (this.data) {
      this.buildForm();
      this.applicationForm.disable();
    }
  }

  // List<AddressBO> activityAddresses = new ArrayList<>();
  // List<KeyValueDTO> activityTypes = new ArrayList<>();
  // String cultureGroupCode;
  // String cultureGroupName;
  // List<PlantProductBO> plantProducts = new ArrayList<>();
  // Boolean plantPassportIssue;
  // Boolean markingIssue;

  buildForm() {
    this.applicationForm = this.fb.group({
      recordId: [this.data.recordId],
      activityAddresses: this.fb.array([]),
      activityTypes: [
        this.data.activityTypes?.map((el: any, index: number) => {
          if (index !== 0) {
            return ' ' + el.name;
          }
          return el.name;
        }),
      ],
      contactPersons: this.fb.array([]),
      plantProducts: this.fb.array([]),
      cultureGroupName: [this.data.cultureGroupName],
      plantPassportIssue: [this.data.plantPassportIssue],
      markingIssue: [this.data.markingIssue],
    });
    if (this.data?.activityAddresses?.length) {
      this.fillActivityAddresses();
    }
    if (this.data?.plantProducts?.length) {
      this.fillPlantProducts();
    }
    if (this.data?.contactPersons?.length) {
      this.fillContactPersons();
    }
  }

  fillActivityAddresses() {
    this.data?.activityAddresses.map((el: any, index: number) => {
      const activityAddressForm = this.fb.group({
        description: el.description,
        fullAddress: el.fullAddress,
        postCode: el.postCode,
        countryName: el.countryName,
      });
      this.activityAddresses.push(activityAddressForm);
    });
  }

  fillPlantProducts() {
    this.data?.plantProducts.map((el: any, index: number) => {
      const plantProductForm = this.fb.group({
        productName: el.productName,
        originName: el.originName,
        productPassport: el.productPassport,
        cultureGroupName: el.cultureGroupName,
      });
      this.plantProducts.push(plantProductForm);
    });
  }

  fillContactPersons() {
    this.data?.contactPersons.map((el: any, index: number) => {
      const contactPersonForm = this.fb.group({
        identifier: el.identifier,
        fullName: el.fullName,
        fullAddress: el.fullAddress,
        phone: el.phone,
        email: el.email,
        contactPersonTypeName: el.contactPersonTypeName,
      }); 
      this.contactPersons.push(contactPersonForm);
    });
  }

  get activityAddresses() {
    return this.applicationForm?.controls['activityAddresses'] as FormArray;
  }

  get plantProducts() {
    return this.applicationForm?.controls['plantProducts'] as FormArray;
  }

  get contactPersons() {
    return this.applicationForm?.controls['contactPersons'] as FormArray;
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
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

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
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

  get isApplicationStatusPaymentConfirmedEnteredOrProcessing() {
    if (
      this.data?.recordStatus === 'PAYMENT_CONFIRMED' ||
      this.data?.recordStatus === 'ENTERED' ||
      this.data?.recordStatus === 'INSPECTION_COMPLETED' ||
      this.data?.recordStatus === 'PROCESSING'
    ) {
      return true;
    }
    return false;
  }

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
  }
}
