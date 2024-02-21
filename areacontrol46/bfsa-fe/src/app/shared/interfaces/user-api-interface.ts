import {UserInterface} from "./user-interface";

export interface UserApiInterface {
  pageable: {
    offset: number;
    sort: {
      empty: true;
      unsorted: true;
      sorted: true;
    };
    paged: true;
    unpaged: true;
    pageNumber: number;
    pageSize: number;
  };
  content: UserInterface[];
}
