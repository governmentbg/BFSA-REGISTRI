import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MessageResourceApiInterface } from '../interfaces/message-resource-api-interface';
import { MessageResourceInterface } from '../interfaces/message-resource-interface';

@Injectable({
  providedIn: 'root',
})
export class MessageResourceService {
  constructor(private http: HttpClient) {}

  getMessages(
    page: string = '0',
    size: string = '0',
    sort: string = 'asc'
  ): Observable<MessageResourceInterface[]> {
    const params = new HttpParams({ fromObject: { page, size, sort } });
    const requestOptions = { params };
    return this.http
      .get<MessageResourceApiInterface>(
        `${environment.apiUrl}/api/message-resources`,
        requestOptions
      )
      .pipe(map((data) => data.results));
  }

  getMessage(code: string): Observable<MessageResourceInterface[]> {
    return this.http.get<MessageResourceInterface[]>(
      `${environment.apiUrl}/api/message-resources/${code}/`
    );
  }

  updateMessage(
    code: string,
    payload: MessageResourceInterface
  ): Observable<MessageResourceInterface> {
    return this.http.put<MessageResourceInterface>(
      `${environment.apiUrl}/api/message-resources/${code}`,
      payload
    );
  }
  updateMessages(
    payload: MessageResourceInterface[]
  ): Observable<MessageResourceInterface[]> {
    return this.http.put<MessageResourceInterface[]>(
      `${environment.apiUrl}/api/message-resources`,
      payload
    );
  }
}
