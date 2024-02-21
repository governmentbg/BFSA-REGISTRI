export class Nomenclature {
  code: string;
  description?: string;
  name?: string;
  enabled: boolean;
  parentCode: string;
  subNomenclatures: Nomenclature[];
}
