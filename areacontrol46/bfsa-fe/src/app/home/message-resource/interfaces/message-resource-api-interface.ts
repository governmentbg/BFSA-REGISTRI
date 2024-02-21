import { MessageResourceInterface } from './message-resource-interface';

export interface MessageResourceApiInterface {
  totalCount: number;
  results: MessageResourceInterface[];
}
