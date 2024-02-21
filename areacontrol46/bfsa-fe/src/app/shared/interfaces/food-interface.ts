export interface FoodInterface {
  id: string;
  name: string;
  enabled: boolean;
  description: string;
  subFoods: FoodInterface[];
}
