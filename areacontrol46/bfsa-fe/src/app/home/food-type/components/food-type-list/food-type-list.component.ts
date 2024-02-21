import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { FoodTypeInterface } from '../../interfaces/food-type-interface';
import { FoodTypeService } from '../../services/food-type.service';
import { FoodTypeDialogComponent } from '../food-type-dialog/food-type-dialog.component';

export enum ClassifiersCodeList {
  foodTypeCode = '0001000',
}

@Component({
  selector: 'app-food-type-list',
  templateUrl: './food-type-list.component.html',
  styleUrls: ['./food-type-list.component.scss'],
})
export class FoodTypeListComponent implements OnInit {
  foodTypes: FoodTypeInterface[];

  constructor(
    private router: Router,
    public dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private foodTypeService: FoodTypeService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.foodTypeService
      .getSubFoodTypes(ClassifiersCodeList.foodTypeCode)
      .subscribe((foodTypes: FoodTypeInterface[]) => {
        this.foodTypes = foodTypes;
      });
  }

  addFoodType() {
    const dialogRef = this.dialog.open(FoodTypeDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((foodType: FoodTypeInterface) => {
      if (foodType) {
        console.log(foodType);
        this.foodTypeService
          .addSubFoodType(ClassifiersCodeList.foodTypeCode, foodType)
          .subscribe({
            next: (res) => {
              this.initialize();
              this._snackBar.open('Food Type added successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while adding the food type: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }
}
