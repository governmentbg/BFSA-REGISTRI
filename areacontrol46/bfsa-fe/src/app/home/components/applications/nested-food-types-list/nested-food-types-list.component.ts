import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-nested-food-types-list',
  templateUrl: './nested-food-types-list.component.html',
  styleUrls: ['./nested-food-types-list.component.scss'],
})
export class NestedFoodTypesListComponent {
  @Input() foodTypes: FoodTypeInterface[];
  @Input() areFoodTypesEnabled: boolean;
  @Output()
  nestedFoodTypes: EventEmitter<any> = new EventEmitter<any>();

  selectedFoodType(foodType: FoodTypeInterface, checked: boolean) {
    this.nestedFoodTypes.emit({ foodType, checked });
  }
}
