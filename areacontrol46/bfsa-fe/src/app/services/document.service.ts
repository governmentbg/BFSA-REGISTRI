import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  constructor(private http: HttpClient) {}

  downloadDocumentByRecordId(recordId: string) {
    const headers = new HttpHeaders({
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions = {
      headers,
      responseType: 'blob',
      observe: 'response',
    };
    return this.http.get(
      `${environment.apiUrl}/api/documents/${recordId}/export-paper`,
      requestOptions as any
    );
  }

  downloadRefusalDocumentByServiceType(serviceType: string, recordId: string) {
    const headers = new HttpHeaders({
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions = {
      headers,
      responseType: 'blob',
      observe: 'response',
    };
    return this.http.get(
      `${environment.apiUrl}/api/documents/${recordId}/export-refusal-order-${serviceType}`,
      requestOptions as any
    );
  }

  downloadOrderByRecordId(recordId: string) {
    const headers = new HttpHeaders({
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });
    const requestOptions = {
      headers,
      responseType: 'blob',
      observe: 'response',
    };
    return this.http.get(
      `${environment.apiUrl}/api/documents/${recordId}/export-order`,
      requestOptions as any
    );
  }
}
