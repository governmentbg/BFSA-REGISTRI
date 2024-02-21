export interface InspectionInterface {
  id: string;
  description: string;
  users: string[];
  facilityId: string;
  vehicleId: string;
  recordId: string;
  riskLevel: string;
  reasonsCodes: string[];
  endDate: Date;
  status: string;
  inspectionType: string;
  applicantIdentifier: string;
  applicantFullName: string;
  attachments: [
    {
      id: string;
      code: string;
      key: string;
      name: string;
      description: string;
      value: string;
    }
  ];
}
