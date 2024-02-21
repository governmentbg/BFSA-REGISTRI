import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, map, Observable, shareReplay, Subject, tap } from 'rxjs';
import { environment } from 'src/environments/environment';
import { LanguageApiInterface } from '../shared/interfaces/language-api-interface';
import { LanguageInterface } from '../shared/interfaces/language-interface';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  constructor(private http: HttpClient) {}

  getLangs(): Observable<LanguageApiInterface> {
    const headers = new HttpHeaders({
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions = { headers };
    return this.http
      .get<LanguageApiInterface>(
        `${environment.apiUrl}/api/auth/langs`,
        requestOptions
      )
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          // next: (val) => console.log('getLangs: ', val),
          error: (err) => console.error('getLangs: ', err),
        }),
        shareReplay(1)
      );
  }

  getLang(id: string): Observable<LanguageInterface> {
    return this.http
      .get<LanguageInterface>(`${environment.apiUrl}/api/langs/${id}`)
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('getLang: ', val),
          error: (err) => console.error('getLang: ', err),
        }),
        shareReplay(1)
      );
  }

  updateLang(id: String, payload: {}): Observable<LanguageInterface> {
    return this.http
      .put<LanguageInterface>(`${environment.apiUrl}/api/langs/${id}`, payload)
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('updateLang: ', val),
          error: (err) => console.error('updateLang: ', err),
        }),
        shareReplay(1)
      );
  }

  createLang(payload: {}): Observable<LanguageInterface> {
    return this.http
      .post<LanguageInterface>(`${environment.apiUrl}/api/langs/`, payload)
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('createLang: ', val),
          error: (err) => console.error('createLang: ', err),
        }),
        shareReplay(1)
      );
  }
}
