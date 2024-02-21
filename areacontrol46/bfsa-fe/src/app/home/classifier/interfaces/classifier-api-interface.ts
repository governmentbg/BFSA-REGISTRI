import { ClassifierInterface } from './classifier-interface';

export interface ClassifierApiInterface {
  totalCount: number;
  results: ClassifierInterface[];
}
