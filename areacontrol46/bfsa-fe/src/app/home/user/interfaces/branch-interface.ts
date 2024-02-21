import { User } from '../../branch/interfaces/branch-interface';

export interface BranchInterface {
  id: string;
  identifier: string;
  settlementCode: string;
  email: string;
  phone1: string;
  phone2: string;
  phone3: string;
  name: string;
  address: string;
  description: string;
  main: boolean;
  enabled: boolean;
  users: User[];
}
