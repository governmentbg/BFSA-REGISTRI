import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MatDialogRef,
  MAT_DIALOG_DATA,
  MatDialog,
} from '@angular/material/dialog';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { AddressService } from 'src/app/services/address.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { BranchInterface } from 'src/app/home/user/interfaces/branch-interface';
import { ClassifierService } from 'src/app/home/classifier/services/classifier.service';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';

@Component({
  selector: 'app-edit-application-s2272',
  templateUrl: './edit-application-s2272.component.html',
  styleUrls: ['./edit-application-s2272.component.scss'],
})
export class EditApplicationS2272Component {
  public applicationForm: FormGroup;
  public foodTypes: FoodTypeInterface[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public settlements: SettlementInterface[] | null;
  public branches: BranchInterface[];
  public municipalitySettlements: SettlementInterface[];
  public areaSettlements: SettlementInterface[];
  public correspondenceMunicipalitySettlements: SettlementInterface[];
  public correspondenceSettlements: SettlementInterface[] | null;
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public measuringUnits: NomenclatureInterface[];
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';
  public finallySelectedFoodTypes = new Map();

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<EditApplicationS2272Component>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private settlementService: SettlementService,
    private nomenclatureService: NomenclatureService,
    private readonly branchService: BranchService,
    public dialog: MatDialog,
    private readonly addressService: AddressService,
    private readonly recordService: RecordService,
    public refusalDialog: MatDialog
  ) {}

  ngOnInit() {
    this.getAllVehicleTypes();
    this.getAllRequestorAuthorTypes();
    this.getAllBranches();
    this.getAllSettlements();
    this.getAllVehicleOwnerships();
    this.getMeasuringUnits();

    if (this.data) {
      this.applicationForm = this.fb.group({
        recordId: [this.data.recordId],
        vehicles: this.fb.array([]),
      });
      if (this.data?.vehicles?.length) {
        this.fillVehicles();
      }
      this.fillAddresses();
      this.applicationForm.disable();
    }
  }

  closeDialog() {
    this.dialogRef.close();
  }

  fillAddresses() {
    this.addressService
      .getAddressInfo(this.data.applicantCorrespondenceAddress?.id)
      .subscribe((res: any) => {
        this.applicationForm
          .get('applicantCorrespondenceAddress')
          ?.get('settlementCode')
          ?.setValue(res);
      });
  }

  getVehicleFoodTypes(index: number) {
    const vehicleFormGroup = (
      this.applicationForm.get('vehicles') as FormArray
    ).at(index) as FormGroup;
    return vehicleFormGroup.get('foodTypes')?.value;
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
        const vehicleFoodTypes: any[] = [];
        const vehicles = (this.applicationForm.get('vehicles') as FormArray)
          .value;
        vehicles.forEach((vehicle: any, index: number, array: []) => {
          vehicleFoodTypes.push({
            identifier: vehicle.registrationPlate,
            foodTypes: Array.from(this.finallySelectedFoodTypes.get(index)),
          });
        });
        this.recordService
          .approveRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            vehicleFoodTypes
          )
          .subscribe({
            next: (res) => {
              console.log(res);
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

  fillVehicles() {
    this.data?.vehicles.map((el: any, index: number) => {
      this.finallySelectedFoodTypes.set(index, new Set(el.foodTypes));

      const vehicleForm = this.fb.group({
        licenseNumber: [el.licenseNumber],
        vehicleTypeCode: [el.vehicleTypeCode],
        brandModel: [el.brandModel],
        registrationPlate: [el.registrationPlate],
        vehicleOwnershipTypeCode: [el.vehicleOwnershipTypeCode],
        load: [el.load],
        volume: [el.volume],
        loadUnitCode: [el.loadUnitCode],
        volumeUnitCode: [el.volumeUnitCode],
        foodTypes: [el.foodTypes],
      });
      this.vehicles.push(vehicleForm);
    });
  }

  get vehicles() {
    return this.applicationForm?.controls['vehicles'] as FormArray;
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
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

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
    });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
      });
  }

  private getAllVehicleOwnerships() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01600')
      .subscribe((res) => {
        this.vehicleOwnerships = res;
      });
  }

  private getAllVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res) => {
        this.vehicleTypes = res;
      });
  }

  onToggleChange(
    data: { foodType: FoodTypeInterface; checked: boolean },
    vehicleIndex: number
  ) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;
    let foundFoods = this.finallySelectedFoodTypes.get(vehicleIndex);
    if (!isChecked) {
      foundFoods.forEach((foundFood: FoodTypeInterface) => {
        if (foundFood.code === code) {
          foundFoods.delete(foundFood);
        }
      });
    } else {
      foundFoods.add(foodType);
    }
    console.log(foundFoods)
  }
}
