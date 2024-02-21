export interface FacilityInterface {
  id: string;
  name: string;
  contractorId: string;
  registerCode: string;
  branchId: string;
  activityTypeCode: string;
  activityTypeName: string;
  facilityTypeCode: string;
  facilityTypeName: string;
  waterSupplyTypeCode: string;
  activityDescription: string;
  disposalWasteWater: string;
  permission177: string;
  capacity: number;
  subActivityTypeCode: string;
  periodCode: string;
  address: Address;
  facilityCapacities: any[];
  relatedActivityCategories: any[];
  associatedActivityCategories: any[];
  animalSpecies: any[];
  remarks: any[];
  pictograms: any[];
  regNumber: string;
  facilityPaperRegNumbers: string;
}

export interface Address {
  id: string;
  address: string;
  enabled: boolean;
  settlementCode: string;
}
