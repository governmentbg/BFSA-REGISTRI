import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { forkJoin, map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ContractorInterface } from '../interfaces/contractor-interface';
import { FacilityInterface } from '../interfaces/facility-interface';
import { NomenclatureInterface } from '../interfaces/nomenclature-interface';
import { VehicleInterface } from '../interfaces/vehicle-interface';

@Injectable({
  providedIn: 'root',
})
export class RegisterService {
  constructor(private http: HttpClient) {}

  getContractor(id: string): Observable<ContractorInterface> {
    return this.http.get<ContractorInterface>(
      `${environment.apiUrl}/api/contractors/${id}`
    );
  }

  getFacilities(id: string): Observable<FacilityInterface[]> {
    return this.http.get<FacilityInterface[]>(
      `${environment.apiUrl}/api/facilities/contractor-id/${id}`
    );
  }

  getFacilityInfoById(id: string): Observable<FacilityInterface[]> {
    return this.http.get<FacilityInterface[]>(
      `${environment.apiUrl}/api/facilities/${id}`
    );
  }

  getContractorFacilities(id: string): Observable<FacilityInterface[]> {
    return this.http.get<FacilityInterface[]>(
      `${environment.apiUrl}/api/contractors/${id}/contractor-facilities`
    );
  }

  getVehiclesForFacility(id: string): Observable<any[]> {
    return this.http.get<any[]>(
      `${environment.apiUrl}/api/facilities/${id}/vehicles`
    );
  }

  getVehiclesForContractor(id: string): Observable<VehicleInterface[]> {
    return this.http.get<VehicleInterface[]>(
      `${environment.apiUrl}/api/contractors/${id}/vehicles`
    );
  }

  getContractorPapers(id: string): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/contractor-papers/${id}`
    );
  }

  getContractorDataByApplicantId(id: string): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/contractor-papers/${id}`
    );
  }

  getPlantProtectionServices(id: string) {
    return forkJoin([
      this.http.get(
        `${environment.apiUrl}/api/s2699-applications/applicant/${id}`
      ),
      this.http.get(
        `${environment.apiUrl}/api/s1590-applications/applicant/${id}`
      ),
      this.http.get(
        `${environment.apiUrl}/api/s2700-applications/applicant/${id}`
      ),
    ]).pipe(map((res) => res.flat()));
  }

  getDistanceTradingForContractor(id: string): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/contractors/${id}/distance-trading`
    );
  }

  getNomenclature(code: string): Observable<NomenclatureInterface> {
    return this.http.get<NomenclatureInterface>(
      `${environment.apiUrl}/api/noms/${code}`
    );
  }

  getFoodAdditives(applicantId: string): Observable<any> {
    return this.http.get<any>(
      `${environment.apiUrl}/api/food-supplements/applicant/${applicantId}`
    );
  }

  getAdjuvants(applicantId: string) {
    return this.http.get<any>(
      `${environment.apiUrl}/api/s2698-applications/applicant/${applicantId}`
    );
  }

  getFertilizers(applicantId: string) {
    return this.http.get<any>(
      `${environment.apiUrl}/api/s2170-applications/applicant/${applicantId}`
    );
  }

  getSeeds(applicantId: string) {
    return this.http.get<any>(
      `${environment.apiUrl}/api/s3363-applications/applicant/${applicantId}`
    );
  }

  updateVehicleStatus(
    vehicleId: string,
    body: { status: string; vehicleId: string }
  ): Observable<any> {
    return this.http.put<any>(
      `${environment.apiUrl}/api/vehicles/${vehicleId}/update-status`,
      body
    );
  }

  updateFacilityStatus(
    id: string,
    body: { status: string; id: string }
  ): Observable<any> {
    return this.http.put<any>(
      `${environment.apiUrl}/api/facilities/${id}/update-status`,
      body
    );
  }
}
