import { Component, Inject, OnInit } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { CreateApplicationS2701Component } from '../create-application-s2701/create-application-s2701.component';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import * as saveAs from 'file-saver';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import { DocumentService } from 'src/app/services/document.service';

@Component({
  selector: 'app-edit-application-s2170',
  templateUrl: './edit-application-s2170.component.html',
  styleUrls: ['./edit-application-s2170.component.scss'],
})
export class EditApplicationS2170Component implements OnInit {
  public applicationForm: FormGroup;
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<CreateApplicationS2701Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private readonly recordService: RecordService,
    public dialog: MatDialog,
    private readonly documentService: DocumentService
  ) {}

  ngOnInit() {
    if (this.data) {
      this.buildForm();
    }
  }

  buildForm() {
    this.applicationForm = this.fb.group({
      recordId: [this.data.recordId],
      supplier: this.fb.group({
        fullName: [this.data.supplier?.fullName],
        identifier: [this.data.supplier?.identifier],
        fullAddress: [this.data.supplier?.address.fullAddress],
        email: [this.data.supplier?.email],
      }),
      manufacturer: this.fb.group({
        fullName: [this.data.manufacturer?.fullName],
        identifier: [this.data.manufacturer?.identifier],
        fullAddress: [this.data.manufacturer?.address.fullAddress],
        email: [this.data.manufacturer?.email],
      }),
      manufactureAddress: this.fb.group({
        fullAddress: [this.data.manufactureAddress?.fullAddress],
      }),
      manufacturePlace: [this.data.manufacturePlace],
      name: [this.data.name],

      nameLat: [this.data.nameLat],
      productCategoryName: [this.data.productCategoryName],
      productTypeName: [this.data.productTypeName],
      euMarketPlacementCountryName: [this.data.euMarketPlacementCountryName],
      materials: this.fb.array([]),
      processingDescription: [this.data.processingDescription],
      processingDescriptionPatentNumber: [
        this.data.processingDescriptionPatentNumber,
      ],

      ingredients: this.fb.array([]),

      physicalStateName: [
        this.data.physicalStateName + ', ' + this.data.physicalName,
      ],
      drySubstance: [this.data.drySubstance],
      inorganicSubstance: [this.data.inorganicSubstance],
      organicSubstance: [this.data.organicSubstance],

      arsen: [this.data.arsen],
      nickel: [this.data.nickel],
      cadmium: [this.data.cadmium],
      mercury: [this.data.mercury],
      chrome: [this.data.chrome],
      lead: [this.data.lead],
      ph: [this.data.ph],
      livingOrganisms: this.fb.array([]),

      enterococci: [this.data.enterococci],
      enterococciColi: [this.data.enterococciColi],
      clostridiumPerfringens: [this.data.clostridiumPerfringens],
      salmonella: [this.data.salmonella],
      staphylococus: [this.data.staphylococus],
      aspergillus: [this.data.aspergillus],
      nematodes: [this.data.nematodes],
      expectedEffect: [this.data.expectedEffect],

      crops: this.fb.array([]),

      possibleMixes: [this.data.possibleMixes],
      notRecommendedMixes: [this.data.notRecommendedMixes],
      notRecommendedClimaticConditions: [
        this.data.notRecommendedClimaticConditions,
      ],
      notRecommendedSoilConditions: [this.data.notRecommendedSoilConditions],
      prohibitedImportCrops: [this.data.prohibitedImportCrops],

      storage: [this.data.storage],
      transport: [this.data.transport],
      fire: [this.data.fire],
      humanAccident: [this.data.humanAccident],
      spilliageAccident: [this.data.spilliageAccident],
      handlingDeactivationOption: [this.data.handlingDeactivationOption],
    });
    if (this.data?.materials?.length) {
      this.fillМaterials();
    }
    if (this.data?.ingredients?.length) {
      this.fillIngredients();
    }
    if (this.data?.livingOrganisms?.length) {
      this.fillLivingOrganisms();
    }
    if (this.data?.crops?.length) {
      this.fillCrops();
    }
    this.applicationForm.disable();
  }

  fillМaterials() {
    this.data?.materials.map((el: any, index: number) => {
      const materialForm = this.fb.group({
        materialTypeName: el.materialTypeName,
        name: el.name,
        amount: el.amount,
        origin: el.origin,
      });
      this.materials.push(materialForm);
    });
  }

  fillIngredients() {
    this.data?.ingredients.map((el: any, index: number) => {
      const ingredientForm = this.fb.group({
        type: el.type,
        name: el.name,
        amount: el.amount,
      });
      this.ingredients.push(ingredientForm);
    });
  }

  fillLivingOrganisms() {
    this.data?.livingOrganisms.map((el: any, index: number) => {
      const livingOrganismForm = this.fb.group({
        livingOrganismName: el.livingOrganismName,
        classification: el.classification,
        strain: el.strain,
        type: el.type,
        cfu: el.cfu,
      });
      this.livingOrganisms.push(livingOrganismForm);
    });
  }

  fillCrops() {
    this.data?.crops.map((el: any, index: number) => {
      const cropForm = this.fb.group({
        cropName: el.cropName,
        cropCode: el.cropCode,
        croptTypeCode: el.cropTypeCode,
        cropTypeName: el.cropTypeName,
        cropDoses: this.fb.array(
          el.cropDoses.map((el: any) => {
            return this.fb.group({
              applicationName: el.applicationName,
              doseMin: el.doseMin,
              doseMax: el.doseMax,
              doseUnitName: el.doseUnitName,
              treatmentMin: el.treatmentMin,
              treatmentMax: el.treatmentMax,
              mixtureConcentrationMin: el.mixtureConcentrationMin,
              mixtureConcentrationMax: el.mixtureConcentrationMax,
              concentrationDoseUnitName: el.concentrationDoseUnitName,
              deadline: el.deadline,
            });
          })
        ),
      });
      this.crops.push(cropForm);
    });
  }

  getCropDosesArrayAtIndex(cropIndex: number): FormArray {
    const cropDose = (this.applicationForm.get('crops') as FormArray).at(
      cropIndex
    ) as FormGroup;
    return cropDose.get('cropDoses') as FormArray;
  }

  get materials() {
    return this.applicationForm?.controls['materials'] as FormArray;
  }

  get ingredients() {
    return this.applicationForm?.controls['ingredients'] as FormArray;
  }

  get livingOrganisms() {
    return this.applicationForm?.controls['livingOrganisms'] as FormArray;
  }

  get crops() {
    return this.applicationForm?.controls['crops'] as FormArray;
  }

  openErrorsDialog() {
    const dialogRef = this.dialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
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
}
