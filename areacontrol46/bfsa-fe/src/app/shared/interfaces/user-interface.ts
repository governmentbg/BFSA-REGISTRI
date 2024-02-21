export interface UserInterface {
  id?: string;
  email: string;
  fullName: string;
  username: string;
  identifier: string;
  enabled: boolean;
  roles: [string];
}
