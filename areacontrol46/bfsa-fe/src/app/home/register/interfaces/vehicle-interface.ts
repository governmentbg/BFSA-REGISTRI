export interface VehicleInterface {
  id: string;
  registrationPlate: string;
  entryNumber: string;
  entryDate: string;
  certificateNumber: string;
  certificateDate: string;
  load: number;
  loadUnitCode: string;
  volume: number;
  volumeUnitCode: string;
  name: string;
  description: string;
  branchId: string;
  enabled: boolean;
  vehicleTypeCode: string;
  vehicleTypeName: string;
  vehicleOwnershipTypeCode: string;
  registerCodes: string[];
  foodTypes: string[];
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
