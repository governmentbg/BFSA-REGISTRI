export interface NomenclatureInterface {
  code: string;
  description: string;
  name: string;
  enabled: boolean;
  parentCode: string;
  subNomenclatures: NomenclatureInterface[];
  symbol: any;
}
