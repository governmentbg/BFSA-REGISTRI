import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { BranchApiInterface } from '../interfaces/branch-api-interface';
import { BranchInterface } from '../interfaces/branch-interface';

@Injectable({
  providedIn: 'root',
})
export class BranchService {
  constructor(private http: HttpClient) {}

  getBranches(): Observable<BranchInterface[]> {
    return this.http
      .get<BranchApiInterface>(`${environment.apiUrl}/api/branches/`)
      .pipe(map((data) => data.results));
  }

  getBranche(id: string): Observable<BranchInterface> {
    return this.http.get<BranchInterface>(
      `${environment.apiUrl}/api/branches/${id}`
    );
  }

  updateBranch(id: string, payload: {}): Observable<BranchInterface> {
    return this.http.put<BranchInterface>(
      `${environment.apiUrl}/api/branches/${id}`,
      payload
    );
  }

  addBranch(payload: BranchInterface): Observable<BranchInterface> {
    return this.http.post<BranchInterface>(
      `${environment.apiUrl}/api/branches/create`,
      payload
    );
  }
}
