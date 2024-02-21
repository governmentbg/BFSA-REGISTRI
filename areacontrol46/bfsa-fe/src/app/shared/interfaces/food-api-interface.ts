import {FoodInterface} from "./food-interface";

export interface FoodApiInterface {
  totalCount: number;
  results: FoodInterface[];
}
