import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { NomenclatureApiInterface } from '../interfaces/nomenclature-api-interface';
import { NomenclatureInterface } from '../interfaces/nomenclature-interface';

@Injectable({
  providedIn: 'root',
})
export class NomenclatureService {
  constructor(private http: HttpClient) {}

  getNomenclatures(): Observable<NomenclatureInterface[]> {
    return this.http
      .get<NomenclatureApiInterface>(`${environment.apiUrl}/api/noms`)
      .pipe(map((data) => data.results));
  }

  getSubNomenclatures(code: string): Observable<NomenclatureInterface[]> {
    return this.http
      .get<NomenclatureApiInterface>(
        `${environment.apiUrl}/api/noms/${code}/sub-nomenclatures`
      )
      .pipe(map((data) => data.results));
  }

  getNomenclature(code: string): Observable<NomenclatureInterface> {
    return this.http.get<NomenclatureInterface>(
      `${environment.apiUrl}/api/noms/${code}`
    );
  }

  addNomenclature(payload: NomenclatureInterface) {
    return this.http.post<any>(
      `${environment.apiUrl}/api/noms/create-next`,
      payload
    );
  }

  addSubNomenclature(code: string, payload: NomenclatureInterface) {
    return this.http.post<any>(
      `${environment.apiUrl}/api/noms/${code}/create-next`,
      payload
    );
  }

  updateNomenclature(code: string, payload: NomenclatureInterface) {
    return this.http.put<any>(
      `${environment.apiUrl}/api/noms/${code}`,
      payload
    );
  }
}
