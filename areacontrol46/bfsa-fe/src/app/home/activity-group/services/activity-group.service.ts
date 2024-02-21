import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { ActivityGroupApiInterface } from '../interfaces/activity-group-api-interface';
import { ActivityGroupInterface } from '../interfaces/activity-group-interface';

@Injectable({
  providedIn: 'root',
})
export class ActivityGroupService {
  constructor(private http: HttpClient) {}

  getActivityGroups(): Observable<ActivityGroupInterface[]> {
    return this.http
      .get<ActivityGroupApiInterface>(
        `${environment.apiUrl}/api/activity-groups/parents`
      )
      .pipe(map((data) => data.results));
  }

  getActivityGroup(id: string): Observable<ActivityGroupInterface> {
    return this.http.get<ActivityGroupInterface>(
      `${environment.apiUrl}/api/activity-groups/${id}`
    );
  }

  updateActivityGroup(
    id: string,
    payload: any
  ): Observable<ActivityGroupInterface> {
    return this.http.put<ActivityGroupInterface>(
      `${environment.apiUrl}/api/activity-groups/${id}`,
      payload
    );
  }

  addActivityGroup(
    payload: ActivityGroupInterface
  ): Observable<ActivityGroupInterface> {
    return this.http.post<ActivityGroupInterface>(
      `${environment.apiUrl}/api/activity-groups/create`,
      payload
    );
  }

  getSubActivityGroups(id: string): Observable<ActivityGroupInterface[]> {
    return this.http
      .get<ActivityGroupApiInterface>(
        `${environment.apiUrl}/api/activity-groups/${id}/sub-activity-groups`
      )
      .pipe(map((data) => data.results));
  }

  addSubActivityGroup(
    id: string,
    payload: ActivityGroupInterface
  ): Observable<ActivityGroupInterface> {
    return this.http.post<ActivityGroupInterface>(
      `${environment.apiUrl}/api/activity-groups/${id}/create`,
      payload
    );
  }
}
