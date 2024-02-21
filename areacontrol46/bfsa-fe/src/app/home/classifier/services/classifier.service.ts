import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ClassifierApiInterface } from '../interfaces/classifier-api-interface';
import { ClassifierInterface } from '../interfaces/classifier-interface';
import { FoodTypeInterface } from '../../food-type/interfaces/food-type-interface';

@Injectable({
  providedIn: 'root',
})
export class ClassifierService {
  constructor(private http: HttpClient) {}

  getClassifiers(): Observable<ClassifierInterface[]> {
    return this.http
      .get<ClassifierApiInterface>(`${environment.apiUrl}/api/classifiers`)
      .pipe(map((data) => data.results));
  }

  getSubClassifiers(code: string): Observable<ClassifierInterface[]> {
    return this.http
      .get<ClassifierApiInterface>(
        `${environment.apiUrl}/api/classifiers/${code}/sub-classifiers`
      )
      .pipe(map((data) => data.results));
  }

  getClassifier(code: string): Observable<ClassifierInterface> {
    return this.http.get<ClassifierInterface>(
      `${environment.apiUrl}/api/classifiers/${code}`
    );
  }

  updateClassifier(
    code: string,
    payload: ClassifierInterface
  ): Observable<ClassifierInterface> {
    return this.http.put<ClassifierInterface>(
      `${environment.apiUrl}/api/classifiers/${code}`,
      payload
    );
  }

  addClassifier(payload: ClassifierInterface): Observable<ClassifierInterface> {
    return this.http.post<ClassifierInterface>(
      `${environment.apiUrl}/api/classifiers/create-next`,
      payload
    );
  }

  addSubClassifier(
    code: string,
    payload: ClassifierInterface
  ): Observable<ClassifierInterface> {
    return this.http.post<ClassifierInterface>(
      `${environment.apiUrl}/api/classifiers/${code}/create-next`,
      payload
    );
  }
}
