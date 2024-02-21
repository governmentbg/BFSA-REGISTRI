import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { BuildInfoInterface } from '../interfaces/build-info-interface';

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  constructor(private http: HttpClient) {}

  getBuildInfo(): Observable<BuildInfoInterface> {
    return this.http.get<BuildInfoInterface>(
      `${environment.apiUrl}/api/auth/build-info`
    );
  }
}
