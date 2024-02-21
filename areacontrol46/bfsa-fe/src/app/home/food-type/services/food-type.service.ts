import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { environment } from 'src/environments/environment';
import { FoodTypeApiInterface } from '../interfaces/food-type-api-interface';
import { FoodTypeInterface } from '../interfaces/food-type-interface';

@Injectable({
  providedIn: 'root',
})
export class FoodTypeService {
  constructor(private http: HttpClient) {}

  getFoodTypes(): Observable<FoodTypeInterface[]> {
    return this.http
      .get<FoodTypeApiInterface>(`${environment.apiUrl}/api/classifiers/`)
      .pipe(map((data) => data.results[0].subClassifiers));
  }

  getSubFoodTypes(code: string): Observable<FoodTypeInterface[]> {
    return this.http
      .get<FoodTypeApiInterface>(
        `${environment.apiUrl}/api/classifiers/${code}/sub-classifiers`
      )
      .pipe(map((data) => data.results));
  }

  getFoodType(code: string): Observable<FoodTypeInterface> {
    return this.http.get<FoodTypeInterface>(
      `${environment.apiUrl}/api/classifiers/${code}`
    );
  }

  updateFoodType(
    code: string,
    payload: FoodTypeInterface
  ): Observable<FoodTypeInterface> {
    return this.http.put<FoodTypeInterface>(
      `${environment.apiUrl}/api/classifiers/${code}`,
      payload
    );
  }

  addFoodType(payload: FoodTypeInterface): Observable<FoodTypeInterface> {
    return this.http.post<FoodTypeInterface>(
      `${environment.apiUrl}/api/classifiers/create-next`,
      payload
    );
  }

  addSubFoodType(
    code: string,
    payload: FoodTypeInterface
  ): Observable<FoodTypeInterface> {
    return this.http.post<FoodTypeInterface>(
      `${environment.apiUrl}/api/classifiers/${code}/create-next`,
      payload
    );
  }
}
