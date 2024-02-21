export interface FoodTypeInterface {
  code: string;
  parent: FoodTypeInterface;
  name: string;
  enabled: boolean;
  description: string;
  symbol: string;
  subClassifiers: FoodTypeInterface[];
}
