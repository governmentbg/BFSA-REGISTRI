export interface InspectionFacility {
  description: string;
  users: string[];
  facilityId: string;
  endDate: Date;
  inspectionType: string;
  reasonsCodes: string[];
}
