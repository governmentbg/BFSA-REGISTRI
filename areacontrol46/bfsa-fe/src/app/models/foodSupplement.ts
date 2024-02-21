import { Nomenclature } from './nomenclature';

export class FoodSupplement {
  foodSupplementType: Nomenclature;
  name?: string;
  purpose?: string;
  supplement: boolean;
  recDailyDose: string;
  measuringUnit: Nomenclature;
  markedAnotherEUState: boolean;
  country: Nomenclature;
}
