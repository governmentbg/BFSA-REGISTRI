import {
  HttpBackend,
  HttpClient,
  HttpHeaders,
  HttpParams,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { TokenStorageService } from 'src/app/services/token.service';
import { environment } from 'src/environments/environment';
import { SettlementApiInterface } from '../interfaces/settlement-api-interface';
import { SettlementInterface } from '../interfaces/settlement-interface';

@Injectable({
  providedIn: 'root',
})
export class SettlementService {
  constructor(
    private http: HttpClient,
    private httpBackend: HttpBackend,
    private tokenService: TokenStorageService
  ) {}

  getSettlements(): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(`${environment.apiUrl}/api/settlements/`)
      .pipe(map((data) => data.results));
  }

  getParentSettlements(): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/settlements/parents`
      )
      .pipe(map((data) => data.results));
  }

  getSettlement(code: string): Observable<SettlementInterface> {
    return this.http.get<SettlementInterface>(
      `${environment.apiUrl}/api/settlements/${code}`
    );
  }

  getSettlementInfo(code: string): Observable<string> {
    return this.http.get(`${environment.apiUrl}/api/`, {
      responseType: 'text',
    });
  }

  getChildrenSettlements(
    parentCode: string
  ): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/settlements/${parentCode}/sub-settlements`
      )
      .pipe(map((data) => data.results));
  }

  updateSettlement(
    code: string,
    payload: SettlementInterface
  ): Observable<SettlementInterface> {
    return this.http.put<SettlementInterface>(
      `${environment.apiUrl}/api/settlements/${code}`,
      payload
    );
  }

  addSettlement(payload: SettlementInterface): Observable<SettlementInterface> {
    return this.http.post<SettlementInterface>(
      `${environment.apiUrl}/api/settlements/create`,
      payload
    );
  }

  getMunicipalitiesByCode(code: string): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/settlements/${code}/region-settlements`
      )
      .pipe(map((data) => data.results));
  }

  getRegionsByCode(code: string): Observable<SettlementInterface[]> {
    return this.http
      .get<SettlementApiInterface>(
        `${environment.apiUrl}/api/settlements/${code}/municipality-settlements`
      )
      .pipe(map((data) => data.results));
  }

  searchSettlements(q: string): Observable<SettlementInterface[]> {
    const newHttpClient = new HttpClient(this.httpBackend);
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + token,
      // 'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const params = new HttpParams({ fromObject: { q } });
    const requestOptions = { headers, params };
    return newHttpClient.get<SettlementInterface[]>(
      `${environment.apiUrl}/api/settlements`,
      requestOptions
    );
  }

  getInfo(code: string): Observable<string> {
    const newHttpClient = new HttpClient(this.httpBackend);
    const token = this.tokenService.getToken();
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + token,
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions: Object = { headers, responseType: 'text' };
    return newHttpClient.get<string>(
      `${environment.apiUrl}/api/settlements/${code}/info`,
      requestOptions
    );
  }
}
