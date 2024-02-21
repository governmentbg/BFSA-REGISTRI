export interface ActivityGroupInterface {
  id: string;
  name: string;
  description: string;
  enabled: boolean;
  parentCode: string;
  revisionMetadata: RevisionMetadata;
  relatedActivityCategories: RelatedActivityCategory[];
  associatedActivityCategories: AssociatedActivityCategory[];
  animalSpecies: AnimalSpecie[];
  remarks: Remark[];
  subActivityGroups: ActivityGroupInterface[];
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

export interface RelatedActivityCategory {
  id: string;
  code: string;
  key: string;
  name: string;
  description: string;
  value: string;
}

export interface AssociatedActivityCategory {
  id: string;
  code: string;
  key: string;
  name: string;
  description: string;
  value: string;
}

export interface AnimalSpecie {
  id: string;
  code: string;
  key: string;
  name: string;
  description: string;
  value: string;
}

export interface Remark {
  id: string;
  code: string;
  key: string;
  name: string;
  description: string;
  value: string;
}
