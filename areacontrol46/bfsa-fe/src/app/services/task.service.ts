import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable, shareReplay, tap } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  constructor(private http: HttpClient) {}

  getTasks(): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };

    return this.http
      .get<any>(`${environment.apiUrl}/api/tasks/`, requestOptions)
      .pipe(
        // delay(2000),
        map((data) => {
          return data.results;
        }),
        tap({
          next: (val) => console.log('getTasks: ', val),
          error: (err) => console.error('getTasks: ', err),
        }),
        shareReplay(1)
      );
  }

  getCurrentUserTasks(id: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };

    return this.http
      .get<any>(
        `${environment.apiUrl}/api/tasks/current-user/${id}`,
        requestOptions
      )
      .pipe(
        // delay(2000),
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('getCurrentUserTasks: ', val),
          error: (err) => console.error('getCurrentUserTasks: ', err),
        }),
        shareReplay(1)
      );
  }

  claimTask(taskId: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http
      .put<any>(
        `${environment.apiUrl}/api/tasks/claim/${taskId}`,
        requestOptions
      )
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('claimTask: ', val),
          error: (err) => console.error('claimTask: ', err),
        }),
        shareReplay(1)
      );
  }

  cancelTask(taskId: string): Observable<any> {
    const headers = new HttpHeaders({
      'Accept-Language': 'bg',
    });
    const requestOptions = { headers };
    return this.http
      .put<any>(
        `${environment.apiUrl}/api/tasks/cancel/${taskId}`,
        requestOptions
      )
      .pipe(
        map((data) => {
          return data;
        }),
        tap({
          next: (val) => console.log('cancelTask: ', val),
          error: (err) => console.error('cancelTask: ', err),
        }),
        shareReplay(1)
      );
  }
}
