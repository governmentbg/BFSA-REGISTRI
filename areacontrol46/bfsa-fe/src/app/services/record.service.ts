import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { S2701Interface } from '../home/components/applications/intefaces/s2701-interface';
import { InspectionInterface } from '../home/inspection/interfaces/inspection-interface';
import { S2272Interface } from '../home/components/applications/intefaces/s2272-interface';

@Injectable({
  providedIn: 'root',
})
export class RecordService {
  constructor(private http: HttpClient) {}

  registerApplicationS3180(body: any): Observable<any[]> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/s3180-applications/register`,
      body,
      requestOptions
    );
  }

  updateApplicationS3180(body: any, recordId: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/s3180-applications/activity-description/${recordId}`,
      body,
      requestOptions
    );
  }

  registerApplicationS2701(body: S2701Interface): Observable<S2701Interface> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<S2701Interface>(
      `${environment.apiUrl}/api/s2701-applications/register`,
      body,
      requestOptions
    );
  }

  registerApplicationS2702(body: S2701Interface): Observable<S2701Interface> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<S2701Interface>(
      `${environment.apiUrl}/api/s2702-applications/register`,
      body,
      requestOptions
    );
  }

  registerApplicationS1590(body: S2701Interface): Observable<S2701Interface> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<S2701Interface>(
      `${environment.apiUrl}/api/s1590-applications/register`,
      body,
      requestOptions
    );
  }

  registerApplicationS2699(body: S2701Interface): Observable<S2701Interface> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<S2701Interface>(
      `${environment.apiUrl}/api/s2699-applications/register`,
      body,
      requestOptions
    );
  }

  registerApplicationS2272(body: S2272Interface): Observable<S2272Interface> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<S2272Interface>(
      `${environment.apiUrl}/api/s2272-applications/register`,
      body,
      requestOptions
    );
  }

  attachCertificateImageS2701(id: string, body: FormData): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'multipart/form-data',
    });
    const requestOptions = { headers };
    return this.http.post<S2701Interface>(
      `${environment.apiUrl}/api/s2701-applications/${id}/attach`,
      body,
      requestOptions
    );
  }

  updateApplicationS2701(body: any): Observable<any[]> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/s2701-applications/${body.recordId}/education`,
      body,
      requestOptions
    );
  }

  getRecordAttachments(recordId: String) {
    return this.http.get<any>(
      `${environment.apiUrl}/api/records/${recordId}/attachments`
    );
  }

  registerApplicationS3181(body: any): Observable<any[]> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/s3181-applications/register`,
      body,
      requestOptions
    );
  }

  registerApplicationS3182(body: any): Observable<any[]> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/s3182-applications/register`,
      body,
      requestOptions
    );
  }

  getAllRecords(): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };

    return this.http.get<any>(
      `${environment.apiUrl}/api/records/`,
      requestOptions
    );
  }

  getRecordsByBranchId(
    branchId: string,
    page: number,
    size: number
  ): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };

    return this.http.get<any>(
      `${environment.apiUrl}/api/records/branch/${branchId}?page=${page}&size=${size}`,
      requestOptions
    );
  }

  getRecordbyIdAndServiceType(
    serviceType: string,
    id: string
  ): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };

    return this.http.get<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${id}`,
      requestOptions
    );
  }

  directRegistration(recordId: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/s3180-applications/activity-description/${recordId}`,
      requestOptions
    );
  }

  sendForPayment(recordId: string, recordPrice: number): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/records/${recordId}/send-for-payment?recordPrice=${recordPrice}`,
      requestOptions
    );
  }

  confirmRecordPayment(recordId: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/records/${recordId}/confirm-payment`,
      requestOptions
    );
  }

  search(payload: { q: string; recordStatus: string }): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.get<any>(
      `${environment.apiUrl}/api/records?q=${payload.q}&recordStatus=${payload.recordStatus}`,
      requestOptions
    );
  }

  createInspection(
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.post<InspectionInterface>(
      `${environment.apiUrl}/api/records/${payload.recordId}/create-inspection`,
      payload
    );
  }

  approveRegistrationByServiceType(
    serviceType: string,
    recordId: string,
    facilityFoodTypes?: any[]
  ) {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${recordId}/approve`,
      facilityFoodTypes,
      requestOptions
    );
  }

  approveRegistrationBody(
    serviceType: string,
    body: { recordId: string; orderNumber: number; orderDate: Date }
  ) {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${body.recordId}/approve`,
      body,
      requestOptions
    );
  }

  downloadOrderBody(
    serviceType: string,
    body: { recordId: string; orderNumber: number; orderDate: Date }
  ) {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });

    const requestOptions = {
      headers,
      responseType: 'blob',
      observe: 'response',
    };

    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${body.recordId}/order`,
      body,
      requestOptions as any
    );
  }

  refuseRegistrationByServiceType(serviceType: string, recordId: string) {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${recordId}/refuse`,

      requestOptions
    );
  }

  forCorrectionRegistrationByServiceType(
    serviceType: string,
    recordId: string,
    body: { id: string; description: string }
  ) {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http.put<any>(
      `${environment.apiUrl}/api/${serviceType}-applications/${recordId}/for-correction`,
      body,
      requestOptions
    );
  }
}
