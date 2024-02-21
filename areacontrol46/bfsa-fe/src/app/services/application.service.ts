import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ApplicationService {
  constructor(private http: HttpClient) {}

  sendForPayment(
    serviceType: string,
    recordId: string,
    recordPrice: number
  ): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${recordId}/send-for-payment?recordPrice=${recordPrice}`,
      requestOptions
    );
  }

  confirmApplicationPayment(
    recordId: string,
    serviceType: string
  ): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${recordId}/confirm-payment`,
      ///s502-applications/{recordId}/confirm-payment
      requestOptions
    );
  }
}
