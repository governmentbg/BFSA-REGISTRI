import { Component, Inject } from '@angular/core';
import { FormArray, FormGroup, UntypedFormBuilder } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogRef,
} from '@angular/material/dialog';
import { RecordService } from 'src/app/services/record.service';
import { ErrorsDialogComponent } from '../errors-dialog/errors-dialog.component';
import { ApprovalDialogComponent } from '../approval-dialog/approval-dialog.component';
import { ForCorrectionDialogComponent } from '../for-correction-dialog/for-correction-dialog.component';
import { RefusalDialogComponent } from '../refusal-dialog/refusal-dialog.component';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-edit-application-s13661',
  templateUrl: './edit-application-s13661.component.html',
  styleUrls: ['./edit-application-s13661.component.scss'],
})
export class EditApplicationS13661Component {
  public applicationForm: FormGroup;
  public finalActionErrors: String = '';
  public finallySelectedFoodTypes = new Map();

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    public dialog: MatDialog,
    public dialogRef: MatDialogRef<EditApplicationS13661Component>,
    public refusalDialog: MatDialog,
    private recordService: RecordService,
    private fb: UntypedFormBuilder
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
      facilityRegNumber: [this.data?.facilityRegNumber],
      recipientName: [this.data?.recipientName],
      recipientCountryName: [this.data?.recipientCountryName],
      recipientAddress: [this.data?.recipientAddress],
      borderCrossingName: [this.data?.borderCrossingName],
      products: this.fb.array([]),
    });
    if (this.data.products?.length) {
      this.fillProducts();
    }
  }

  fillProducts() {
    this.data?.products.map((el: any, index: number) => {
      this.finallySelectedFoodTypes.set(index, new Set(el.foodTypes));
      const productForm = this.fb.group({
        id: [el.id],
        foodTypes: [el.foodTypes],
        productName: [el.productName],
        productTrademark: [el.productTrademark],
        productTotalNetWeight: [el.productTotalNetWeight],
        productTotalNetWeightUnitName: [el.productTotalNetWeightUnitName],
        batches: this.fb.array(
          el.batches.map((el: any) => {
            return this.fb.group({
              unitsInBatch: el.unitsInBatch,
              batchNumber: el.batchNumber,
              perUnitNetWeight: el.perUnitNetWeight,
              perUnitNetWeightUnitName: el.perUnitNetWeightUnitName,
              batchNetWeight: el.batchNetWeight,
              batchNetWeightUnitName: el.batchNetWeightUnitName,
            });
          })
        ),
      });
      this.products.push(productForm);
    });
  }

  getProductFoodTypes(index: number) {
    const productFormGroup = (
      this.applicationForm.get('products') as FormArray
    ).at(index) as FormGroup;
    return productFormGroup.get('foodTypes')?.value;
  }

  getProductBatchesAtIndex(productIndex: number) {
    const productFormGroup = (
      this.applicationForm?.get('products') as FormArray
    ).at(productIndex) as FormGroup;
    return productFormGroup.get('batches') as FormArray;
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

  get products() {
    return this.applicationForm?.controls['products'] as FormArray;
  }

  openErrorsDialog() {
    const dialogRef = this.refusalDialog.open(ErrorsDialogComponent, {
      data: this.data.errors,
    });
    dialogRef.afterClosed().subscribe((result) => {
      console.log(result);
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
        const productFoodTypes: any[] = [];
        const products = (this.applicationForm.get('products') as FormArray)
          .value;
        products.forEach((product: any, index: number, array: []) => {
          productFoodTypes.push({
            identifier: product.id,
            foodTypes: Array.from(this.finallySelectedFoodTypes.get(index)),
          });
        });
        this.recordService
          .approveRegistrationByServiceType(
            this.data.serviceType.toLowerCase(),
            this.applicationForm.get('recordId')?.value,
            productFoodTypes
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

  onToggleChange(
    data: { foodType: FoodTypeInterface; checked: boolean },
    productIndex: number
  ) {
    const code = data.foodType.code;
    const foodType = data.foodType;
    const isChecked = data.checked;

    let foundFoods = this.finallySelectedFoodTypes.get(productIndex);
    if (!isChecked) {
      foundFoods.forEach((foundFood: FoodTypeInterface) => {
        if (foundFood.code === code) {
          foundFoods.delete(foundFood);
        }
      });
    } else {
      foundFoods.add(foodType);
    }
  }
}
