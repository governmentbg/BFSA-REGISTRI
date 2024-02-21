import { ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import {
  FormArray,
  FormControl,
  FormGroup,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FacilityStatus } from '../../interfaces/facility-status';
import { RegisterService } from '../../services/register.service';
import { NomenclatureInterface } from '../../interfaces/nomenclature-interface';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { ActivityService } from 'src/app/services/activity.service';
import { BranchService } from 'src/app/services/branch.service';
import {
  ActivityGroupInterface,
  AnimalSpecie,
  Remark,
} from 'src/app/home/activity-group/interfaces/activity-group-interface';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
@Component({
  selector: 'app-view-s3180-dialog',
  templateUrl: './view-s3180-dialog.component.html',
  styleUrls: ['./view-s3180-dialog.component.scss'],
})
export class ViewS3180DialogComponent implements OnInit {
  public status: FormControl = new FormControl('', Validators.required);

  public dialogForm: FormGroup;
  public facilityStatusList = FacilityStatus;
  public measuringUnits: NomenclatureInterface[];
  public vehicleOwnerships: NomenclatureInterface[];
  public vehicleTypes: NomenclatureInterface[];
  public activityTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public periods: NomenclatureInterface[];
  public rawMilkTypes: NomenclatureInterface[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public activityGroupParents: ActivityGroupInterface[];
  public areaSettlements: SettlementInterface[];
  public branches: NomenclatureInterface[];
  public pictograms: NomenclatureInterface[];
  public relatedActivityCategories: any;
  public subActivityGroups: ActivityGroupInterface[];
  public associatedActivityCategories: any;
  public remarks: Remark[];
  public animalSpecies: AnimalSpecie[];

  public productionCode = '01806';
  public commerceCode = '01807';
  public milkProductsCode = '01808';

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ViewS3180DialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: any,
    private readonly registerService: RegisterService,
    private nomenclatureService: NomenclatureService,
    private activityService: ActivityService,
    private readonly branchService: BranchService,
    private readonly cdr: ChangeDetectorRef
  ) {
    dialogRef.disableClose = true;
  }

  get vehicles() {
    return this.dialogForm.controls['vehicles'] as FormArray;
  }

  get facilityCapacities() {
    return this.dialogForm?.get('facilityCapacities') as FormArray;
  }

  ngOnInit(): void {
    if (this.data?.sectionGroupId) {
      this.activityService
        .getSubActivityGroups(this.data.sectionGroupId)
        .subscribe((res) => {
          this.subActivityGroups = res;
        });
    }

    this.getAllRequestorAuthorTypes();
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
    console.log(this.data);

    if (this.data) {
      this.status.setValue(this.data.status);
      this.dialogForm = this.fb.group({
        regNumber: this.data.regNumber,
        facilityPaperRegNumbers: this.data.facilityPaperRegNumbers,
        name: this.data.name,
        activityTypeName: this.data.activityTypeName,
        facilityTypeName: this.data.facilityTypeName,
        status: this.data.status,
        id: this.data.id,
        sectionGroupId: [this.data?.sectionGroupId],
        activityGroupId: [this.data?.activityGroupId],
        foodTypes: [[]],
        relatedActivityCategories: [this.data?.relatedActivityCategories],
        associatedActivityCategories: [this.data?.associatedActivityCategories],
        animalSpecies: [this.data?.animalSpecies],
        remarks: [this.data?.remarks],
        pictograms: [this.data?.pictograms],
        facilityId: [this.data.facilityId],
        permission177: [this.data.permission177],

        activityDescription: [
          {
            value: this.data.activityDescription,
            disabled: true,
          },
        ],

        subActivityTypeName: [
          {
            value: this.data.subActivityTypeName,
            disabled: true,
          },
        ],
        waterSupplyTypeCode: [
          {
            value: this.data.waterSupplyTypeCode,
            disabled: true,
          },
        ],
        disposalWasteWater: [
          {
            value: this.data.disposalWasteWater,
            disabled: true,
          },
        ],
        measuringUnitCode: [this.data.measuringUnitCode],
        periodCode: [this.data.periodCode],
        capacity: [this.data.capacity],
        facilityCapacities: this.fb.array([]),
        description: [this.data.description, [Validators.required]],
        address: this.fb.group({
          phone: this.data?.address?.phone,
          fullAddress: this.data?.address?.fullAddress,
          mail: this.data?.address?.mail,
          url: this.data?.address?.url,
        }),

        vehicles: this.fb.array([]),

        branchIdentifier: [this.data?.branchIdentifier],
      });
    }
    if (this.data?.vehicles?.length > 0) {
      this.fillVehicles();
    }
    this.dialogForm.disable();
  }

  changeFacilityStatus() {
    this.registerService
      .updateFacilityStatus(this.data.id, {
        status: this.status.value,
        id: this.data.id,
      })
      .subscribe((res) => console.log(res));
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

  get dataContainAdressDTO() {
    return this.data?.address?.settlementCode;
  }

  onSubmit() {
    this.registerService
      .updateFacilityStatus(this.data.item.id, this.dialogForm.value)
      .subscribe((res) => {
        this.dialogRef.close(res);
      });
  }

  private getParentActivityGroups() {
    this.activityService
      .getActivityGroupParents()
      .subscribe((res) => (this.activityGroupParents = res));
  }

  private getSubActivityGroups() {
    this.activityService
      .getActivityGroupParents()
      .subscribe((res) => (this.activityGroupParents = res));
  }

  private getActivityTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.activityTypes = response;
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

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measuringUnits = response;
      });
  }

  private getWaterSupplyTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02400')
      .subscribe((response: NomenclatureInterface[]) => {
        this.waterSupplyTypes = response;
      });
  }

  private getPeriods() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02800')
      .subscribe((response: NomenclatureInterface[]) => {
        this.periods = response;
      });
  }

  private getRawMilkTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.rawMilkTypes = response;
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

  private getAllPictograms() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01200')
      .subscribe((res) => {
        this.pictograms = res;
      });
  }
}
