import { ContractorAddress } from './s2701-interface';

export interface S2272Interface {
  requestorIdentifier: string;
  requestorFullName: string;
  requestorEmail: string;
  requestorAuthorTypeCode: string;
  entityType: string;
  applicantIdentifier: string;
  applicantFullName: string;
  applicantEmail: string;
  applicantPhone: string;
  applicantPostCode: string;
  applicantAddress: string;
  applicantCorrespondenceAddress: ContractorAddress;
  branchIdentifier: string;
  serviceType: string;
  recordPrice: number;
  recordStatus: string;
  approvalDocumentStatus: string;
  educationalDocumentType: string;
  educationalDocumentNumber: string;
  educationalDocumentDate: string;
  educationalInstitution: string;
  description: string;
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
