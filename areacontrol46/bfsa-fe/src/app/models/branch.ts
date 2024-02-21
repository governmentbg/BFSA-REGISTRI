import { UserInterface } from '../shared/interfaces/user-interface';

export class Branch {
  address: string;
  description?: string;
  email?: string;
  enabled: boolean;
  id: string;
  main: boolean;
  name: string;
  phone1: string;
  phone2: string;
  phone3: string;
  settlementCode: string;
  users: UserInterface[];
}
