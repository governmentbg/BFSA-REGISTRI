import { ChangeDetectorRef, Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class LoaderService {
  public isLoading = new Subject<boolean>();

  constructor() {}

  show() {
    this.isLoading.next(true);
  }

  hide() {
    this.isLoading.next(false);
  }

  public getLoaderStatus(): Observable<boolean> {
    return this.isLoading.asObservable();
  }
}
