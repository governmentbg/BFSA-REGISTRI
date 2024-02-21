import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FoodTypeInterface } from 'src/app/home/food-type/interfaces/food-type-interface';

@Component({
  selector: 'app-food-types-checkbox-list',
  templateUrl: './food-types-checkbox-list.component.html',
  styleUrls: ['./food-types-checkbox-list.component.scss'],
})
export class FoodTypesCheckboxListComponent {
  @Input() foodTypes: FoodTypeInterface[];
  @Input() areFoodTypesEnabled: boolean;
  @Output()
  selectedFoodTypes: EventEmitter<any> = new EventEmitter<any>();
  public foodTypesMap: Map<string, FoodTypeInterface[]> = new Map();
  public lastFoodTypeName = 'Тип храна';
  public currentMapElementKey = '';

  iterateFoodTypesAndFillMap(foodTypes: FoodTypeInterface[]) {
    foodTypes.map((foodType) => this.fillParentAndChildMap(foodType));
  }

  fillParentAndChildMap(foodType: FoodTypeInterface) {
    const key = this.constructMapKey(foodType.parent);
    if (!this.foodTypesMap.get(key)) {
      this.foodTypesMap.set(key, []);
      this.foodTypesMap.get(key)?.push(foodType);
    } else if (this.foodTypesMap.get(key)) {
      this.foodTypesMap.get(key)?.push(foodType);
    }
  }

  constructMapKey(foodType: FoodTypeInterface): string {
    if (foodType.name !== this.lastFoodTypeName) {
      this.currentMapElementKey =
        foodType.name + ', ' + this.currentMapElementKey;
      return this.constructMapKey(foodType.parent);
      //remove recursive calling if we want to show the first parent only
    }
    let result = this.currentMapElementKey;
    this.currentMapElementKey = '';
    return result.slice(0, -2);
    // we slice here to remove the ', ' from the end of the key
  }

  ngOnChanges() {
    if (this.foodTypes.length) {
      this.iterateFoodTypesAndFillMap(this.foodTypes);
    }
  }

  selectedFoodType(data: { foodType: FoodTypeInterface; checked: boolean }) {
    this.selectedFoodTypes.emit(data);
  }
}
