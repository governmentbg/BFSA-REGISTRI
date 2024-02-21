export interface UserInterface {
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
  inspections: string[];
  directorateCode: string;
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
