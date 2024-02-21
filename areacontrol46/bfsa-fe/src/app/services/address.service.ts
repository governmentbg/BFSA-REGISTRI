import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AddressService {
  constructor(private http: HttpClient) {}

  getAddressInfo(code: string): Observable<string> {
    return this.http.get(
      `${environment.apiUrl}/api/addresses/${code}/address-info`,
      {
        responseType: 'text',
      }
    );
  }
}
