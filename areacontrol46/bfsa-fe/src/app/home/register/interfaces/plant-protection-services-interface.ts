export interface PlantProtectionServicesInterface {
  branchIdentifier: string;
  entityType: string;
  requestorIdentifier: string;
  requestorFullName: string;
  requestorEmail: string;
  requestorAuthorTypeCode: string;
  applicantIdentifier: string;
  applicantFullName: string;
  applicantEmail: string;
  applicantPhone: string;
  applicantCorrespondenceAddress: ApplicantCorrespondenceAddress;
  recordId: string;
  serviceType: string;
  recordPrice: number;
  recordStatus: string;
  errors: any[];
  seedTreatmentFacilityAddress: SeedTreatmentFacilityAddress;
  ch83CertifiedPerson: TreatmentSeedControlPerson;
  ch83CertifiedPersons: TreatmentSeedPerformingPerson[];
}

export interface ApplicantCorrespondenceAddress {
  id: string;
  address: string;
  fullAddress: string;
  postCode: string;
  addressTypeCode: string;
  enabled: boolean;
  settlementCode: string;
}

export interface SeedTreatmentFacilityAddress {
  id: string;
  address: string;
  fullAddress: string;
  settlementCode: string;
}

export interface TreatmentSeedControlPerson {
  fullName: string;
  type: string;
  entityType: string;
  id: string;
  identifier: string;
  enabled: boolean;
  contractorRelations: any[];
  relatedActivityCategories: any[];
  registerCodes: any[];
}

export interface TreatmentSeedPerformingPerson {
  fullName: string;
  type: string;
  entityType: string;
  id: string;
  identifier: string;
  enabled: boolean;
  contractorRelations: any[];
  relatedActivityCategories: any[];
  registerCodes: any[];
}
