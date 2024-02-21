import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ActivityService {
  constructor(private http: HttpClient) {}

  getActivityGroupParents() {
    return this.http
      .get(`${environment.apiUrl}/api/activity-groups/parents`)
      .pipe(map((data: any) => data.results));
  }

  getSubActivityGroups(parentId: string) {
    return this.http
      .get(
        `${environment.apiUrl}/api/activity-groups/${parentId}/sub-activity-groups`
      )
      .pipe(map((data: any) => data.results));
  }

  getActivityGroupById(id: string) {
    return this.http
      .get(`${environment.apiUrl}/api/activity-groups/${id}`)
  }
}
