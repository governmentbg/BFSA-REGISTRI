import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({ providedIn: 'root' })
export class NomenclatureService {
  constructor(private http: HttpClient) {}

  getAllNomenclatures() {
    return this.http.get<any>(`${environment.apiUrl}/api/noms/`, {
      withCredentials: true,
    });
  }

  addCategory(body: { name: string; description: string }) {
    return this.http.post<any>(
      `${environment.apiUrl}/api/noms/create-next`,
      body
    );
  }

  addNomenclature(
    body: { name: string; description: string },
    categoryCode: string
  ) {
    return this.http.post<any>(
      `${environment.apiUrl}/api/noms/${categoryCode}/create-next`,
      body
    );
  }

  editNomenclature(
    body: { name: string; description: string },
    categoryCode: string
  ) {
    return this.http.put<any>(
      `${environment.apiUrl}/api/noms/${categoryCode}`,
      body
    );
  }

  getNomenclatureByCode(code: string) {
    return this.http.get<any>(`${environment.apiUrl}/api/noms/${code}`);
  }

  getNomenclatureByParentCode(parentCode: string) {
    return this.http
      .get<any>(
        `${environment.apiUrl}/api/noms/${parentCode}/sub-nomenclatures`
      )
      .pipe(map((res) => res.results));
  }
}
