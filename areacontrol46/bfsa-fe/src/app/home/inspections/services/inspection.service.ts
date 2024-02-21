import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { InspectionApiInterface } from '../interfaces/inspection-api-interface';
import { InspectionAttachmentsInterface } from '../interfaces/inspection-attachments-interface';
import { InspectionFacility } from '../interfaces/inspection-facility';
import { InspectionInterface } from '../interfaces/inspection-interface';
import { InspectionVehicle } from '../interfaces/inspection-vehicle';

@Injectable({
  providedIn: 'root',
})
export class InspectionService {
  constructor(private http: HttpClient) {}

  getInspections(
    page: string = '0',
    size: string = '0',
    sort: string = 'asc'
  ): Observable<InspectionInterface[]> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<InspectionApiInterface>(
        `${environment.apiUrl}/api/inspections`,
        requestOptions
      )
      .pipe(map((data) => data.content));
  }

  getInspection(id: string): Observable<InspectionInterface> {
    return this.http.get<InspectionInterface>(
      `${environment.apiUrl}/api/inspections/${id}`
    );
  }

  getAttachments(id: string): Observable<InspectionAttachmentsInterface[]> {
    return this.http.get<InspectionAttachmentsInterface[]>(
      `${environment.apiUrl}/api/inspections/${id}/attachments`
    );
  }

  createInspection(
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.post<InspectionInterface>(
      `${environment.apiUrl}/api/inspections/create`,
      payload
    );
  }

  createFacilityInspection(
    id: string,
    payload: InspectionFacility
  ): Observable<InspectionFacility> {
    return this.http.post<InspectionFacility>(
      `${environment.apiUrl}/api/facilities/${id}/inspection`,
      payload
    );
  }

  createVehicleInspection(
    id: string,
    payload: InspectionVehicle
  ): Observable<InspectionVehicle> {
    return this.http.post<InspectionVehicle>(
      `${environment.apiUrl}/api/vehicles/${id}/inspection`,
      payload
    );
  }

  updateInspection(
    id: string,
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.put<InspectionInterface>(
      `${environment.apiUrl}/api/inspections/${id}`,
      payload
    );
  }

  completeVehicleInspection(
    vehicleId: string,
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.put<InspectionInterface>(
      `${environment.apiUrl}/api/vehicles/${vehicleId}/complete-inspection`,
      payload
    );
  }

  completeFacilityInspection(
    facilityId: string,
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.put<InspectionInterface>(
      `${environment.apiUrl}/api/facilities/${facilityId}/complete-inspection`,
      payload
    );
  }

  completeInspection(
    id: string,
    payload: InspectionInterface
  ): Observable<InspectionInterface> {
    return this.http.put<InspectionInterface>(
      `${environment.apiUrl}/api/records/${id}/complete-inspection`,
      payload
    );
  }

  attach(id: string, docTypeCode: string, payload: FormData): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'multipart/form-data',
    });
    const requestOptions = { headers };
    return this.http.post<any>(
      `${environment.apiUrl}/api/inspections/${id}/${docTypeCode}/attach`,
      payload,
      requestOptions
    );
  }

  deleteFile(id: string): Observable<string> {
    return this.http.delete<string>(`${environment.apiUrl}/api/files/${id}`);
  }

  getFile(id: string): Observable<any> {
    return this.http.get<any>(`${environment.apiUrl}/api/files/${id}`);
  }
}
