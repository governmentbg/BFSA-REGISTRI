import { Component, Inject } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { VehicleStatus } from '../../interfaces/vehicle-status';
import { RegisterService } from '../../services/register.service';

@Component({
  selector: 'app-contractor-vehicles-dialog',
  templateUrl: './contractor-vehicles-dialog.component.html',
  styleUrls: ['./contractor-vehicles-dialog.component.scss'],
})
export class ContractorVehiclesDialogComponent {
  dialogForm = this.fb.group({
    certificateNumber: [null, Validators.required],
    registrationPlate: [null, Validators.required],
    vehicleTypeName: [null, Validators.required],
    brandModel: [null, Validators.required],
    load: [null, Validators.required],
    entryDate: [null, Validators.required],
    status: [null, Validators.required],
    id: [null],
  });
  vehicleStatusList: VehicleStatus;

  constructor(
    private fb: UntypedFormBuilder,
    public dialogRef: MatDialogRef<ContractorVehiclesDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { item: any; vehicleStatusList: VehicleStatus },
    private readonly registerService: RegisterService
  ) {
    dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    console.log(this.data);

    if (this.data.item) {
      this.dialogForm.patchValue({
        certificateNumber: this.data.item.certificateNumber ?? 'n/a',
        registrationPlate: this.data.item.registrationPlate ?? 'n/a',
        vehicleTypeName: this.data.item.vehicleTypeName ?? 'n/a',
        brandModel: this.data.item.name ?? 'n/a',
        load: this.data.item.load ?? 'n/a',
        entryDate: this.data.item.entryDate ?? 'n/a',
        status: this.data.item.status,
        id: this.data.item.id,
      });
    }

    this.dialogForm
      .get('status')
      ?.valueChanges.subscribe((val) => console.log(val));
  }

  onSubmit() {
    this.registerService
      .updateVehicleStatus(this.data.item.id, this.dialogForm.value)
      .subscribe((res) => {
        this.dialogRef.close(res);
      });
  }
}
