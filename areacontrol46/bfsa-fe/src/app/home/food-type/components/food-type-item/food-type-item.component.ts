import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { FoodTypeInterface } from '../../interfaces/food-type-interface';
import { FoodTypeService } from '../../services/food-type.service';
import { Location } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatDialog } from '@angular/material/dialog';
import { FoodTypeDialogComponent } from '../food-type-dialog/food-type-dialog.component';

@Component({
  selector: 'app-food-type-item',
  templateUrl: './food-type-item.component.html',
  styleUrls: ['./food-type-item.component.scss'],
})
export class FoodTypeItemComponent implements OnInit {
  foodType: FoodTypeInterface;

  constructor(
    private foodTypeService: FoodTypeService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const id = params['id'];
      this.foodTypeService
        .getFoodType(id)
        .subscribe((foodType: FoodTypeInterface) => {
          this.foodType = foodType;
        });
    });
  }

  editFoodType(foodType: FoodTypeInterface) {
    const dialogRef = this.dialog.open(FoodTypeDialogComponent, {
      width: '400px',
      data: { isAdd: false, foodType },
    });
    dialogRef.afterClosed().subscribe((foodType: FoodTypeInterface) => {
      if (foodType) {
        console.log(foodType);
        this.foodTypeService.updateFoodType(foodType.code, foodType).subscribe({
          next: (res) => {
            this.initialize();
            this._snackBar.open('Food type updated successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while updating the food type: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }

  addSubFoodType() {
    const dialogRef = this.dialog.open(FoodTypeDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((foodType: FoodTypeInterface) => {
      if (foodType) {
        console.log(foodType);
        this.foodTypeService
          .addSubFoodType(this.foodType.code, foodType)
          .subscribe({
            next: (res) => {
              this._snackBar.open('Food type added successfully', '', {
                duration: 1000,
              });
              this.initialize();
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

  back(): void {
    this.location.back();
  }
}
