import {NomenclatureInterface} from "./nomenclature-interface";

export interface NomenclatureApiInterface {
  totalCount: number;
  results: NomenclatureInterface[];
}
