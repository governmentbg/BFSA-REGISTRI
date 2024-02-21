import { Component, Inject } from '@angular/core';
import {
  FormArray,
  FormControl,
  FormGroup,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { NomenclatureInterface } from '../../interfaces/nomenclature-interface';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { RegisterService } from '../../services/register.service';
import { FacilityStatus } from '../../interfaces/facility-status';

@Component({
  selector: 'app-view-s3182-dialog',
  templateUrl: './view-s3182-dialog.component.html',
  styleUrls: ['./view-s3182-dialog.component.scss'],
})
export class ViewS3182DialogComponent {
  public status: FormControl = new FormControl('', Validators.required);
  private _facilityStatusList = FacilityStatus;
  public get facilityStatusList() {
    return this._facilityStatusList;
  }
  public set facilityStatusList(value) {
    this._facilityStatusList = value;
  }
  public dialogForm: FormGroup;
  public vehicleTypes: NomenclatureInterface[];
  public activityTypes: NomenclatureInterface[];
  public waterSupplyTypes: NomenclatureInterface[];
  public measurementUnits: NomenclatureInterface[];
  public materialTypes: NomenclatureInterface[];
  public vehicleOwnerships: any[];

  constructor(
    private nomenclatureService: NomenclatureService,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder,
    private readonly registerService: RegisterService
  ) {}

  ngOnInit() {
    console.log(this.data);
    this.getAllVehicleTypes();
    this.getMeasuringUnits();
    this.getMaterialTypes();
    this.getWaterSupplyTypes();
    this.getAllVehicleOwnerships();

    if (this.data) {
      this.status.setValue(this.data.status);
      this.dialogForm = this.fb.group({
        address: this.fb.group({
          phone: [{ value: this.data.address?.phone, disabled: true }],
          mail: [{ value: this.data.address?.mail, disabled: true }],
          url: [{ value: this.data.address?.url, disabled: true }],
          name: [{ value: this.data.address?.name, disabled: true }],
          fullAddress: [
            { value: this.data.address?.fullAddress, disabled: true },
          ],
        }),
        activityTypeName: [this.data.activityTypeName],
        commencementActivityDate: [
          { value: this.data.commencementActivityDate, disabled: true },
        ],
        settlementCode: [
          { value: this.data.address?.settlementCode, disabled: true },
        ],
        phone: [{ value: this.data.phone, disabled: true }],
        mail: [{ value: this.data.mail, disabled: true }],
        url: [{ value: this.data.url, disabled: true }],
        branchIdentifier: [
          { value: this.data.branchIdentifier, disabled: true },
        ],
        vehicles: this.fb.array([]),
        settlementAddressFull: [{ value: '', disabled: true }],
        permission177: [{ value: this.data?.permission177, disabled: true }],
        activityDescription: [
          { value: this.data?.activityDescription, disabled: true },
        ],
        activityTypeCode: [
          { value: this.data?.activityTypeCode, disabled: true },
        ],
        waterSupplyTypeCode: [
          { value: this.data?.waterSupplyTypeCode, disabled: true },
        ],
        disposalWasteWater: [
          { value: this.data?.disposalWasteWater, disabled: true },
        ],
        name: [{ value: this.data?.name, disabled: true }],
        facilityCapacities: this.fb.array([]),
      });
    }
    if (this.data?.facilityCapacities?.length > 0) {
      this.fillFacilityCapacities();
    }
    this.dialogForm.disable();
  }

  private getAllVehicleTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01700')
      .subscribe((res) => {
        this.vehicleTypes = res;
      });
  }

  private getWaterSupplyTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02400')
      .subscribe((response: NomenclatureInterface[]) => {
        this.waterSupplyTypes = response;
      });
  }

  private getMeasuringUnits() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01900')
      .subscribe((response: NomenclatureInterface[]) => {
        this.measurementUnits = response;
      });
  }

  private getMaterialTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('02300')
      .subscribe((response: NomenclatureInterface[]) => {
        //   console.log(response);
        this.materialTypes = response;
      });
  }

  private getAllVehicleOwnerships() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01600')
      .subscribe((res) => {
        this.vehicleOwnerships = res;
      });
  }

  get facilityCapacities() {
    return this.dialogForm?.get('facilityCapacities') as FormArray;
  }

  get vehicles() {
    return this.dialogForm.controls['vehicles'] as FormArray;
  }

  changeFacilityStatus() {
    this.registerService
      .updateFacilityStatus(this.data.id, {
        status: this.status.value,
        id: this.data.id,
      })
      .subscribe((res) => console.log(res));
  }

  fillFacilityCapacities() {
    this.data.facilityCapacities.map((el: any, index: number) => {
      const facilityCapacitiesForm = this.fb.group({
        product: [{ value: el.product, disabled: true }],
        materialCode: [{ value: el.materialCode, disabled: true }],
        quantity: [{ value: el.quantity, disabled: true }],
        unitCode: [{ value: el.unitCode, disabled: true }],
      });
      // console.log(this.facilityCapacities);
      this.facilityCapacities.push(facilityCapacitiesForm);
    });
  }
}
