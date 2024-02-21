export interface ContractorInterface {
  type: string;
  id: string;
  email: string;
  fullName: string;
  username: string;
  password: string;
  identifier: string;
  enabled: boolean;
  roles: string[];
  facilityIds: string[];
  applicantRelations: ContractorRelation[];
  revisionMetadata: RevisionMetadata;
  relatedActivityCategories: string[];
  branchId: string;
  registerCodes: RegisterCode[];
}

export interface RegisterCode {
  code: string;
  name: string;
}

export interface ContractorRelation {
  id: string;
  applicantId: string;
  relationId: string;
  relationTypeCode: string;
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
