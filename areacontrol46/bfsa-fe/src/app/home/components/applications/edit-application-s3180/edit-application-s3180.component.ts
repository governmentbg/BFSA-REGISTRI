import {
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  Inject,
  OnInit,
} from '@angular/core';
import {
  FormArray,
  FormGroup,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import {
  MatDialog,
  MatDialogRef,
  MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { recordStatus } from 'src/app/enums/recordStatus';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { ActivityService } from 'src/app/services/activity.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { RecordService } from 'src/app/services/record.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { DataAndDownloadDialogComponent } from '../data-and-download-dialog/data-and-download-dialog.component';
import { DirectRegisterDialogComponent } from '../direct-register-dialog/direct-register-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { BranchService } from 'src/app/services/branch.service';
import { DocumentService } from 'src/app/services/document.service';
import { saveAs } from 'file-saver';
import { ClassifierService } from 'src/app/home/classifier/services/classifier.service';
import { ClassifierCodeTypeList } from 'src/app/home/classifier/interfaces/classifier-code-type-list';
import { ClassifierInterface } from 'src/app/home/classifier/interfaces/classifier-interface';
import {
  ActivityGroupInterface,
  AnimalSpecie,
  Remark,
} from 'src/app/home/activity-group/interfaces/activity-group-interface';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';
import { InspectionDialogComponent } from 'src/app/home/inspection/components/inspection-dialog/inspection-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { DatePipe } from '@angular/common';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
@Component({
  selector: 'app-edit-application-stepper',
  templateUrl: './edit-application-s3180.component.html',
  styleUrls: ['./edit-application-s3180.component.scss'],
})
export class EditApplicationS3180Component implements AfterViewInit, OnInit {
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public activityGroupParents: ActivityGroupInterface[];
  public animalSpecies: AnimalSpecie[];
  public remarks: Remark[];
  public finallySelectedFoodTypes: { code: string }[] = [];
  public selectedRelatedActivityCategories: any[];
  public relatedActivityCategories: any;
  public associatedActivityCategories: any;
  public branches: any[];
  public pictograms: any[];
  public subActivityGroups: ActivityGroupInterface[];
  public requestorAuthorTypes: NomenclatureInterface[];
  // TODO to set a type on all ^
  public areaSettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[];
  public recordStatuses = Object.entries(recordStatus);
  public recordStatus: string = '';
  public facilityMunicipalities: SettlementInterface[];
  public updatedApplicationState: boolean = false;

  public measuringUnits: NomenclatureInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public activityTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public periods: NomenclatureInterface[];
  public rawMilkTypes: NomenclatureInterface[];

  public productionCode = '01806';
  public commerceCode = '01807';
  public milkProductsCode = '01808';
  public finalActionErrors: String = '';

  get vehicles() {
    return this.applicationForm.controls['vehicles'] as FormArray;
  }

  get facilityCapacities() {
    return this.applicationForm
      .get('facility')
      ?.get('facilityCapacities') as FormArray;
  }

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS3180Component>,
    public dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private activityService: ActivityService,
    private recordService: RecordService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    private readonly documentService: DocumentService,
    private readonly cdr: ChangeDetectorRef,
    private datePipe: DatePipe
  ) {}

  public applicationForm: FormGroup;

  ngOnInit() {
    if (this.data.foodTypes) {
      this.data.foodTypes.map((foodType: FoodTypeInterface) => {
        // fill new array to break reference from array
        this.finallySelectedFoodTypes.push({ code: foodType.code });
      });
    }

    this.buildForm();
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getParentActivityGroups();
    this.getAllPictograms();
    this.getAllBranches();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();
    this.getActivityTypes();
    this.getMeasuringUnits();
    this.getWaterSupplyTypes();
    this.getPeriods();
    this.getRawMilkTypes();
    this.getSubActivityGroups();
  }

  ngAfterViewInit() {
    if (this.data?.sectionGroupId) {
      this.activityService
        .getSubActivityGroups(this.data.sectionGroupId)
        .subscribe((res) => {
          this.subActivityGroups = res;
        });

      this.activityService
        .getActivityGroupById(this.data.sectionGroupId)
        .subscribe((res: any) => {
          if (res.relatedActivityCategories?.length) {
            this.relatedActivityCategories = res.relatedActivityCategories;
            this.associatedActivityCategories = [
              ...res.relatedActivityCategories,
            ];
          }
          this.animalSpecies = res.animalSpecies;
          this.remarks = res.remarks;
        });
    }
    this.applicationForm?.get('sectionGroupId')?.valueChanges.subscribe((val) =>
      this.activityService.getSubActivityGroups(val).subscribe((res) => {
        this.subActivityGroups = res;
      })
    );

    this.applicationForm?.get('sectionGroupId')?.valueChanges.subscribe((val) =>
      this.activityService.getActivityGroupById(val).subscribe((res: any) => {
        this.relatedActivityCategories = res.relatedActivityCategories;
        this.associatedActivityCategories = [...res.relatedActivityCategories];
        this.animalSpecies = res.animalSpecies;
        this.remarks = res.remarks;
      })
    );

    this.applicationForm?.valueChanges.subscribe((val) => {
      this.updatedApplicationState = false;
    });

    if (this.data?.vehicles?.length > 0) {
      this.fillVehicles();
    }

    if (this.data?.facility?.facilityCapacities?.length > 0) {
      this.fillFacilityCapacities();
    }
  }

  private getAllBranches() {
    this.branchService.getBranches().subscribe((response) => {
      this.branches = response.results;
    });
  }

  private getAllPictograms() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01200')
      .subscribe((res) => {
        this.pictograms = res;
      });
  }

  onToggleChange(data: { foodType: any; checked: boolean }) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    if (!isChecked) {
      this.finallySelectedFoodTypes.map(
        (el: { code: string }, index: number) => {
          if (el.code === code) {
            this.finallySelectedFoodTypes.splice(index, 1);
            return console.log(this.finallySelectedFoodTypes);
          }
          return el;
        }
      );
    } else {
      this.finallySelectedFoodTypes.push({ code: foodType.code });
      return console.log(this.finallySelectedFoodTypes);
    }
    return;
  }

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
    });
  }

  public openDirectRegisterDialog() {
    const dialogRef = this.dialog.open(DirectRegisterDialogComponent, {});
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.recordService
          .directRegistration(this.applicationForm.get('recordId')?.value)
          .subscribe((res) => {
            this.dialogRef.close();
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

  public openInspectionDialog() {
    const dialogRef = this.dialog.open(InspectionDialogComponent, {
      width: '500px',
      data: this.applicationForm.getRawValue(),
    });
    dialogRef.afterClosed().subscribe((result) => {
      this.dialogRef.close(true);
    });
  }

  buildForm() {
    if (this.data) {
      this.data.commencementActivityDate = this.datePipe.transform(
        this.data.commencementActivityDate,
        'dd-MM-yyyy'
      );

      this.applicationForm = this.fb.group({
        recordId: [this.data?.recordId],
        sectionGroupId: [this.data?.sectionGroupId],
        activityGroupId: [this.data?.activityGroupId],
        foodTypes: [this.data?.foodTypes],
        relatedActivityCategories: [this.data?.relatedActivityCategories],
        associatedActivityCategories: [this.data?.associatedActivityCategories],
        animalSpecies: [this.data?.animalSpecies],
        remarks: [this.data?.remarks],
        pictograms: [this.data?.pictograms],
        facilityId: [this.data.facilityId],

        facility: this.fb.group({
          permission177: [
            { value: this.data?.facility?.permission177, disabled: true },
          ],

          activityDescription: [
            {
              value: this.data?.facility?.activityDescription,
              disabled: true,
            },
          ],
          activityTypeCode: [
            { value: this.data?.facility?.activityTypeCode, disabled: true },
          ],
          subActivityTypeName: [
            {
              value: this.data?.facility?.subActivityTypeName,
              disabled: true,
            },
          ],
          waterSupplyTypeCode: [
            {
              value: this.data?.facility?.waterSupplyTypeCode,
              disabled: true,
            },
          ],
          disposalWasteWater: [
            {
              value: this.data?.facility?.disposalWasteWater,
              disabled: true,
            },
          ],
          name: [{ value: this.data?.facility?.name, disabled: true }],
          measuringUnitCode: [
            { value: this.data?.facility?.measuringUnitCode, disabled: true },
          ],
          periodCode: [
            { value: this.data?.facility?.periodCode, disabled: true },
          ],
          capacity: [{ value: this.data?.facility?.capacity, disabled: true }],
          facilityCapacities: this.fb.array([]),
          description: [
            this.data?.facility?.description,
            [Validators.required],
          ],
          address: this.fb.group({
            fullAddress: [
              {
                value: this.data?.facility?.address?.fullAddress,
                disabled: true,
              },
            ],
            postCode: [
              {
                value: this.data?.facility?.address?.postCode,
                disabled: true,
              },
            ],
            phone: [
              { value: this.data?.facility?.address?.phone, disabled: true },
            ],
            settlementCode: [
              {
                value: this.data?.facility?.address?.settlementCode,
                disabled: true,
              },
            ],
          }),
        }),

        branchIdentifier: [
          { value: this.data?.branchIdentifier, disabled: true },
        ],
        address: this.fb.group({
          settlementCode: [
            { value: this.data?.address?.settlementCode, disabled: true },
          ],
          address: [{ value: this.data?.address?.address, disabled: true }],
          phone: [{ value: this.data?.address?.phone, disabled: true }],
          url: [{ value: this.data?.address?.url, disabled: true }],
          mail: [{ value: this.data?.address?.mail, disabled: true }],
        }),
        commencementActivityDate: [
          { value: this.data?.commencementActivityDate, disabled: true },
        ],
        vehicles: this.fb.array([]),
      });
      this.recordStatus = this.data?.recordStatus;
    }
  }

  //   openIrregularitiesDialog() {
  //     const dialogRef = this.refusalDialog.open(
  //       IrregularitiesDialogComponent,
  //       {}
  //     );
  //     dialogRef.afterClosed().subscribe((result) => {
  //       if (result) {
  //         //make status active
  //       }
  //     });
  //   }

  downloadFile(file: any, contentType: string, fileName: string) {
    const blob = new Blob([file], {
      type: `${contentType}`,
    });
    saveAs(blob, fileName);
  }

  private getParentActivityGroups() {
    this.activityService.getActivityGroupParents().subscribe((res) => {
      this.activityGroupParents = res;
      this.cdr.detectChanges();
    });
  }

  private getSubActivityGroups() {
    this.activityService.getActivityGroupParents().subscribe((res) => {
      this.activityGroupParents = res;
      this.cdr.detectChanges();
    });
  }

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
        this.cdr.detectChanges();
      });
  }

  private getAllVehicleOwnerships() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01600')
      .subscribe((res) => {
        this.vehicleOwnerships = res;
        this.cdr.detectChanges();
      });
  }

  private getAllVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res) => {
        this.vehicleTypes = res;
        this.cdr.detectChanges();
      });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
        this.cdr.detectChanges();
      });
  }

  private getWaterSupplyTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02400')
      .subscribe((response: NomenclatureInterface[]) => {
        this.waterSupplyTypes = response;
        this.cdr.detectChanges();
      });
  }

  private getPeriods() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.periods = response;
        this.cdr.detectChanges();
      });
  }

  private getRawMilkTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.rawMilkTypes = response;
        this.cdr.detectChanges();
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

  trackSelections(
    selected: boolean,
    relatedActivityCategory: { name: string; code: string }
  ) {
    if (!selected) {
      const index = this.relatedActivityCategories.findIndex(
        (el: any) => el.code == relatedActivityCategory.code
      );
      this.associatedActivityCategories.splice(
        index,
        0,
        relatedActivityCategory
      );
      this.cdr.detectChanges();
    } else {
      this.associatedActivityCategories =
        this.associatedActivityCategories.filter(
          (el: any) => el.code !== relatedActivityCategory.code
        );
      this.cdr.detectChanges();
    }
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm?.get('entityType')?.value === 'PHYSICAL';
  }

  closeDialog() {
    this.dialogRef.close();
  }

  addDays(date: Date, days: number) {
    let result = new Date(date);
    result.setDate(result.getDate() + days);
    return result;
  }

  fillVehicles() {
    this.data.vehicles.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        vehicleOwnershipTypeCode: [
          { value: el.vehicleOwnershipTypeCode, disabled: true },
        ],
        vehicleTypeCode: [{ value: el.vehicleTypeCode, disabled: true }],
        registrationPlate: [{ value: el.registrationPlate, disabled: true }],
        brandModel: [{ value: el.brandModel, disabled: true }],
        certificateNumber: [{ value: el.certificateNumber, disabled: true }],
      });
      this.vehicles.push(vehicleForm);
      this.cdr.detectChanges();
    });
  }

  fillFacilityCapacities() {
    this.data.facility.facilityCapacities.map((el: any, index: number) => {
      const capacityForm = this.fb.group({
        rawMilkTypeCode: [{ value: el.rawMilkTypeCode, disabled: true }],
        fridgeCapacity: [{ value: el.fridgeCapacity, disabled: true }],
      });
      this.facilityCapacities.push(capacityForm);
      this.cdr.detectChanges();
    });
  }

  fixDate() {
    this.applicationForm
      .get('facilityCertRegDate')
      ?.setValue(
        this.addDays(this.applicationForm.get('facilityCertRegDate')?.value, 1)
          .toISOString()
          .slice(0, 10)
      );
  }

  updateApplication() {
    this.fixDate();
    this.applicationForm
      .get('foodTypes')
      ?.setValue(this.finallySelectedFoodTypes);
    console.log(this.applicationForm.value);
    this.recordService
      .updateApplicationS3180(this.applicationForm.value, this.data.recordId)
      .subscribe((res) => {
        if (res) {
          this.recordStatus = res.recordStatus;
          this.updatedApplicationState = true;
        }
      });
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
  }
}
