import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { BranchApiInterface } from '../interfaces/branch-api-interface';
import { BranchInterface } from '../interfaces/branch-interface';
import { UserApiInterface } from '../interfaces/user-api-interface';
import { UserInterface } from '../interfaces/user-interface';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getUsers(
    page: string = '0',
    size: string = '0',
    sort: string = 'asc'
  ): Observable<UserInterface[]> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<UserApiInterface>(`${environment.apiUrl}/api/users/`, requestOptions)
      .pipe(map((data) => data.content));
  }

  getCurrentUser(): Observable<UserInterface> {
    return this.http.get<UserInterface>(`${environment.apiUrl}/api/users/me`);
  }

  getUserByName(username: string = 'admin'): Observable<UserInterface> {
    return this.http.get<UserInterface>(
      `${environment.apiUrl}/api/users/username/${username}`
    );
  }

  searchUsers(
    q: string = 'ad',
    page: string = '0',
    size: string = '1',
    sort: string = 'asc'
  ): Observable<UserInterface[]> {
    const params = new HttpParams({ fromObject: { q, page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<UserApiInterface>(`${environment.apiUrl}/api/users`, requestOptions)
      .pipe(map((data) => data.content));
  }

  getRoles(): Observable<string[]> {
    return this.http.get<string[]>(`${environment.apiUrl}/api/users/roles`);
  }

  updateUser(id: string, payload: {}): Observable<UserInterface> {
    return this.http.put<UserInterface>(
      `${environment.apiUrl}/api/users/${id}`,
      payload
    );
  }

  addUser(payload: UserInterface): Observable<UserInterface> {
    return this.http.post<UserInterface>(
      `${environment.apiUrl}/api/users/`,
      payload
    );
  }

  getBranches(): Observable<BranchInterface[]> {
    return this.http
      .get<BranchApiInterface>(`${environment.apiUrl}/api/branches/`)
      .pipe(map((data) => data.results));
  }
}
