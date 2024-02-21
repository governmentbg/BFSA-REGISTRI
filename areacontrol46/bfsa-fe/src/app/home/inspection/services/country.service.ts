import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class CountryService {
  constructor(private http: HttpClient) {}

  getAllCountries(): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/api/countries/`);
  }

  getEuropeanCountries(): Observable<any[]> {
    const headers = new HttpHeaders({
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions = { headers };
    return this.http.get<any[]>(
      `${environment.apiUrl}/api/countries/eu-members`,
      requestOptions
    );
  }
}
