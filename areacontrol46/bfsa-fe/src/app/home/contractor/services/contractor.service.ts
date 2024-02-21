import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, } from 'rxjs';
import { environment } from 'src/environments/environment';
import { BranchApiInterface } from '../interfaces/branch-api-interface';
import { BranchInterface } from '../interfaces/branch-interface';
import { ContractorApiInterface } from '../interfaces/contractor-api-interface';
import { ContractorInterface } from '../interfaces/contractor-interface';

@Injectable({
  providedIn: 'root',
})
export class ContractorService {
  constructor(private http: HttpClient) {}

  getContractors(
    query: string,
    page: string = '0',
    size: string = '0',
    sort: string = 'asc'
  ): Observable<ContractorInterface[]> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<ContractorApiInterface>(
        `${environment.apiUrl}/api/contractors?q=${query}`,
        requestOptions
      )
      .pipe(map((data) => data.content));
  }

  getAllContractors(
    page: string = '0',
    size: string = '0',
    sort: string = 'asc'
  ): Observable<ContractorInterface[]> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<ContractorApiInterface>(
        `${environment.apiUrl}/api/contractors/`,
        requestOptions
      )
      .pipe(map((data) => data.content));
  }

  updateContractor(id: string, payload: {}): Observable<ContractorInterface> {
    return this.http.put<ContractorInterface>(
      `${environment.apiUrl}/api/contractors/${id}`,
      payload
    );
  }

  getBranches(): Observable<BranchInterface[]> {
    return this.http
      .get<BranchApiInterface>(`${environment.apiUrl}/api/branches/`)
      .pipe(map((data) => data.results));
  }

  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/api/users/roles`);
  }
}
