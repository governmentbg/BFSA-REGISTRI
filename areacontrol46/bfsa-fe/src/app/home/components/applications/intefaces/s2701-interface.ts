export interface S2701Interface {
  requestorLegalRegAddrDistrictCode: string;
  requestorPhysicalCorrAddrCountryCode: string;
  issueDateCaseIdentifier: string;
  requestorIdentifier: string;
  requestorFullName: string;
  requestorEmail: string;
  requestorAuthorTypeCode: string;
  applicantIdentifier: string;
  applicantFullName: string;
  applicantEmail: string;
  applicantPhone: string;
  branchIdentifier: string;
  applicantAddresses: ContractorAddress[];
  recordId: string;
  recordIdentifier: string;
  serviceType: string;
  regNumber: string;
  regDate: string;
  recordPrice: number;
  recordStatus: string;
  approvalDocumentStatus: string;
  educationalDocumentType: string;
  educationalDocumentNumber: string;
  educationalDocumentDate: string;
  educationalInstitution: string;
  description: string;
  discrepancyUntilDate: string;
}

export interface ContractorAddress {
  id: string;
  address: string;
  postCode: string;
  addressLat: string;
  addressTypeCode: string;
  enabled: boolean;
  settlementCode: string;
  mail: string;
  url: string;
  phone: string;
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

export enum ApplicationsAddressTypeCodes {
  HEAD_OFFICE_ADDRESS_TYPE_CODE = '00201',
  CORRESPONDENCE_ADDRESS_TYPE_CODE = '00203',
}
