import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MatDialog,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { BranchInterface } from 'src/app/home/branch/interfaces/branch-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { Nomenclature } from 'src/app/models/nomenclature';
import { BranchService } from 'src/app/services/branch.service';
import { DocumentService } from 'src/app/services/document.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { saveAs } from 'file-saver';
import { ApprovalOrderDateDialogComponent } from '../approval-order-date-dialog/approval-order-date-dialog.component';
import { DataDialogComponent } from '../data-dialog/data-dialog.component';

@Component({
  selector: 'app-edit-application-s2698',
  templateUrl: './edit-application-s2698.component.html',
  styleUrls: ['./edit-application-s2698.component.scss'],
})
export class EditApplicationS2698Component {
  public applicationForm: FormGroup;
  public areaSettlements: SettlementInterface[];
  public requestorAuthorTypes: Nomenclature[];
  public branches: BranchInterface[];
  public finalActionErrors: String = '';
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public pppFunctionCodes: Nomenclature[];

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS2698Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    public refusalDialog: MatDialog,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getAllpppFunctionCodes();
    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        adjuvantName: [this.data.adjuvantName],
        manufacturerName: [this.data.manufacturerName],
        manufacturerIdentifier: [this.data.manufacturerIdentifier],
        supplier: this.fb.group({
          fullName: [this.data.supplier?.fullName],
          identifier: [this.data.supplier?.identifier],
          fullAddress: [this.data.supplier?.address?.fullAddress],
        }),
        effects: [this.data.effects],
        pppFunctionCodes: [this.data.pppFunctionCodes],
        adjuvantProductFormulationTypeName: [
          this.data.adjuvantProductFormulationTypeName,
        ],
        ingredients: this.fb.array([]),
        applications: this.fb.array([]),
      });
      if (this.data?.ingredients?.length) {
        this.fillIngredients();
      }
      if (this.data?.applications?.length) {
        this.fillApplications();
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

  private getAllpppFunctionCodes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('03800')
      .subscribe((res) => {
        console.log(res);
        this.pppFunctionCodes = res;
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
              const dialogRef = this.dialog.open(DataDialogComponent, {
                height: '300px',
                width: '400px',
                data: res,
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

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
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

  fillIngredients() {
    this.data?.ingredients.map((el: any, index: number) => {
      const ingredientForm = this.fb.group({
        ingredientCasNumber: el.ingredientCasNumber,
        ingredientChemName: el.ingredientChemName,
        ingredientContent: el.ingredientContent,
        unitTypeName: el.unitTypeName,
      });
      this.ingredients.push(ingredientForm);
    });
  }

  fillApplications() {
    this.data?.applications.map((el: any, index: number) => {
      const applicationForm = this.fb.group({
        applicationGrainCultureName: el.applicationGrainCultureName,
        applicationDose: el.applicationDose,
        unitTypeName: el.unitTypeName,
      });
      this.applications.push(applicationForm);
    });
  }

  get ingredients() {
    return this.applicationForm?.controls['ingredients'] as FormArray;
  }

  get applications() {
    return this.applicationForm?.controls['applications'] as FormArray;
  }
}
