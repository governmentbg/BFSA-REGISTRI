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

export interface User {
  type: string;
  id: string;
  email: string;
  fullName: string;
  username: string;
  password: string;
  identifier: string;
  enabled: boolean;
  branchId: string;
  roles: string[];
  revisionMetadata: RevisionMetadata;
}

export interface RevisionMetadata {
  revisionNumber: number;
  revisionInstant: string;
  revisionType: string;
  createdBy: string;
  createdDate: string;
  lastModifiedBy: string;
  lastModifiedDate: string;
}
