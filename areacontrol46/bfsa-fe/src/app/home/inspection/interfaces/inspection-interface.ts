export interface InspectionInterface {
  id: string;
  description: string;
  users: string[];
  facilityId: string;
  recordId: string;
  endDate: Date;
  status: string;
  type: string;
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
