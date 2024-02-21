export interface ClassifierInterface {
  code: string;
  parent: ClassifierInterface;
  name: string;
  enabled: boolean;
  description: string;
  symbol: string;
  subClassifiers: ClassifierInterface[];
}
