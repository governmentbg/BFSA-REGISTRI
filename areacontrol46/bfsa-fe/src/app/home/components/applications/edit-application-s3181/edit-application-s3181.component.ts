import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { CountryService } from 'src/app/home/inspection/services/country.service';
import { SettlementInterface } from 'src/app/home/settlement/interfaces/settlement-interface';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchService } from 'src/app/services/branch.service';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { RecordService } from 'src/app/services/record.service';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { AddressService } from 'src/app/services/address.service';
import { DatePipe } from '@angular/common';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

@Component({
  selector: 'app-edit-application-s3181',
  templateUrl: './edit-application-s3181.component.html',
  styleUrls: ['./edit-application-s3181.component.scss'],
})
export class EditApplicationS3181Component {
  public applicationForm: FormGroup;
  public areaSettlements: SettlementInterface[];
  public branches: any[];
  public requestorAuthorTypes: NomenclatureInterface[];
  public facilityMunicipalities: SettlementInterface[];
  public facilitySettlements: SettlementInterface[];
  public municipalitySettlements: SettlementInterface[];
  public settlements: SettlementInterface[];
  public vehicleOwnerships: any[];
  public vehicleTypes: any[];
  public countries: any[];
  public userToken: any = JSON.parse(
    sessionStorage.getItem('auth-user') as any
  );
  public finalActionErrors: String = '';

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: UntypedFormBuilder,
    private readonly countryService: CountryService,
    private settlementService: SettlementService,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS3181Component>,
    private readonly branchService: BranchService,
    private nomenclatureService: NomenclatureService,
    private recordService: RecordService,
    private addressService: AddressService,
    private datePipe: DatePipe,
    public refusalDialog: MatDialog
  ) {}

  ngOnInit() {
    this.getAllRequestorAuthorTypes();
    this.getAllSettlements();
    this.getAllBranches();
    this.getAllVehicleTypes();
    this.getAllVehicleOwnerships();

    if (this.data) {
      this.data.commencementActivityDate = this.datePipe.transform(
        this.data.commencementActivityDate,
        'dd-MM-yyyy'
      );

      this.applicationForm = this.fb.group({
        recordId: [this.data.id],
        regDate: [{ value: this.data.regDate, disabled: true }],
        address: this.fb.group({
          phone: [{ value: this.data.address?.phone, disabled: true }],
          mail: [{ value: this.data.address?.mail, disabled: true }],
          url: [{ value: this.data.address?.url, disabled: true }],
          fullAddress: [
            { value: this.data.address?.fullAddress, disabled: true },
          ],
        }),
        branchIdentifier: [
          { value: this.data.branchIdentifier, disabled: true },
        ],
        facilities: this.fb.array([]),
        foreignFacilityAddresses: this.fb.array([]),
        vehicles: this.fb.array([]),
        commencementActivityDate: [
          { value: this.data.commencementActivityDate, disabled: true },
        ],
      });

      if (this.data?.requestorCorrespondenceAddress) {
        this.settlementService
          .getInfo(this.data.requestorCorrespondenceAddress?.settlementCode)
          .subscribe((res) => {
            this.applicationForm
              .get('requestorCorrespondenceAddress')
              ?.get('settlementCode')
              ?.patchValue(res);
          });
      }
      if (this.data?.applicantCorrespondenceAddress) {
        this.settlementService
          .getInfo(this.data.applicantCorrespondenceAddress?.settlementCode)
          .subscribe((res) => {
            this.applicationForm.controls[
              'correspondenceAddressFull'
            ].patchValue(res);
          });
      }
      if (this.data.facilities?.length) {
        this.fillFacilities();
      }

      // if (this.data.ch50VehicleCertNumbers?.length) {
      //   this.displayUsingFoodTransportByClause50 = true;
      //   this.fillch50VehicleCertNumbers();
      // }

      if (this.data.vehicles?.length) {
        this.fillVehicles();
      }

      if (this.data.foreignFacilityAddresses?.length) {
        this.fillForeignFacilityAddresses();
      }
    }

    this.applicationForm.get('facilityZone')?.valueChanges.subscribe((val) =>
      this.settlementService.getMunicipalitiesByCode(val).subscribe((res) => {
        this.facilityMunicipalities = res;
      })
    );

    this.applicationForm
      .get('facilityMunicipality')
      ?.valueChanges.subscribe((val) =>
        this.settlementService.getRegionsByCode(val).subscribe((res) => {
          this.facilitySettlements = res;
        })
      );

    this.applicationForm.get('applicantZone')?.valueChanges.subscribe((val) =>
      this.settlementService.getMunicipalitiesByCode(val).subscribe((res) => {
        this.municipalitySettlements = res;
      })
    );

    this.applicationForm
      .get('applicantMunicipality')
      ?.valueChanges.subscribe((val) =>
        this.settlementService.getRegionsByCode(val).subscribe((res) => {
          this.settlements = res;
        })
      );
  }

  private getAllSettlements() {
    this.settlementService.getParentSettlements().subscribe((response) => {
      this.areaSettlements = response;
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
  //ForCorrectionDialogComponent

  fillForeignFacilityAddresses() {
    this.data.foreignFacilityAddresses.map((el: any, index: number) => {
      const foreignFacilityForm = this.fb.group({
        countryName: [{ value: el.countryName, disabled: true }],
        address: [{ value: el.address, disabled: true }],
      });
      this.foreignFacilityAddresses.push(foreignFacilityForm);
    });
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
    });
  }

  // fillFacilityPaperNumbers() {
  //   this.data.facilitiesPaperNumbers.map((el: any, index: number) => {
  //     const facilityPaperForm = this.fb.group({
  //       facilityPaperNumber: [{ value: el, disabled: true }],
  //     });
  //     this.facilitiesPaperNumbers.push(facilityPaperForm);
  //   });
  // }

  fillFacilities() {
    this.data.facilities.map((el: any, index: number) => {
      const facilityForm = this.fb.group({
        activityDescription: [
          { value: el.activityDescription, disabled: true },
        ],
        regNumber: [{ value: el.regNumber, disabled: true }],
      });
      this.facilities.push(facilityForm);
    });
  }

  fillCh50VehicleCertNumbers() {
    this.data.ch50VehicleCertNumbers.map((el: any, index: number) => {
      const vehicleForm = this.fb.group({
        vehiclePaperRegNumber: [
          { value: el.vehiclePaperRegNumber, disabled: true },
        ],
      });
      this.ch50VehicleCertNumbers.push(vehicleForm);
    });
  }

  removeVehicle(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.vehicles.removeAt(index);
        }
      });
  }

  removeForeignFacilityAddress(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.foreignFacilityAddresses.removeAt(index);
        }
      });
  }

  removeFacilityPaperNumbers(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.facilities.removeAt(index);
        }
      });
  }

  removech50VehicleCertNumbers(index: number) {
    this.dialog
      .open(ConfirmDialogComponent)
      .afterClosed()
      .subscribe((res) => {
        if (res) {
          this.ch50VehicleCertNumbers.removeAt(index);
        }
      });
  }

  closeDialog() {
    this.dialogRef.close();
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

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
    });
  }

  public get isExpert() {
    return this.userToken?.roles.includes('ROLE_EXPERT');
  }

  public get isPhysicalPersonForm() {
    return this.applicationForm.get('entityType')?.value === 'PHYSICAL';
  }

  get vehicles() {
    return this.applicationForm.controls['vehicles'] as FormArray;
  }

  get facilities() {
    return this.applicationForm.controls['facilities'] as FormArray;
  }

  get ch50VehicleCertNumbers() {
    return this.applicationForm.controls['ch50VehicleCertNumbers'] as FormArray;
  }

  get foreignFacilityAddresses() {
    return this.applicationForm.controls[
      'foreignFacilityAddresses'
    ] as FormArray;
  }

  private getAllRequestorAuthorTypes() {
    this.nomenclatureService
      .getNomenclatureByParentCode('01300')
      .subscribe((res) => {
        this.requestorAuthorTypes = res;
      });
  }
}
