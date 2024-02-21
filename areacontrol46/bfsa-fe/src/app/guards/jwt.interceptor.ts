// import { Injectable } from '@angular/core';
// import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
// import { Observable } from 'rxjs';

// import { AuthenticationService } from '../services/auth.service';
// import { environment } from 'src/environments/environment';

// @Injectable()
// export class JwtInterceptor implements HttpInterceptor {
//     constructor(private authenticationService: AuthenticationService) { }

//     intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
//         // add auth header with jwt if user is logged in and request is to the api url
//         const user = this.authenticationService.userValue;
//         const isLoggedIn = user && user.token;
//         const isApiUrl = request.url.startsWith(environment.apiUrl);
//         if (isLoggedIn && isApiUrl) {
//             request = request.clone({
//                 setHeaders: { Authorization: `Bearer ${user.token}` }
//             });
//         }

//         return next.handle(request);
//     }
// }
import {
  HTTP_INTERCEPTORS,
  HttpEvent,
  HttpErrorResponse,
  HttpHeaders,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpHandler,
  HttpRequest,
} from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { AuthenticationService } from '../services/auth.service';
import { TokenStorageService } from '../services/token.service';
import {Router} from "@angular/router";
const TOKEN_HEADER_KEY = 'Authorization'; // for Spring Boot back-end

@Injectable({ providedIn: 'root' })
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(
    null
  );
  constructor(
    private tokenService: TokenStorageService,
    private authService: AuthenticationService,
    private router: Router
  ) {}
  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<Object>> {
    let authReq = req;
    const token = this.tokenService.getToken();
    if (token != null) {
      authReq = this.addTokenHeader(req, token);
    } else {
      return next.handle(req);
    }
    const headers = new HttpHeaders({
      Authorization: 'Bearer ' + token,
      'Accept-Language': localStorage.getItem('lang') || 'bg',
    });

    const requestCopy = req.clone({ headers });

    return next.handle(requestCopy).pipe(
      catchError((error) => {
        console.log('>>> before redirect: ' + error.status)
        if (
          (error instanceof HttpErrorResponse &&
            !authReq.url.includes('api/auth/') &&
            error.status === 401) ||
          error.status === 403
        ) {
          return this.handle401Error(authReq, next);
        }
        return throwError(error);
      })
    );
  }
  private handle401Error(request: HttpRequest<any>, next: HttpHandler) {
    // if (!this.isRefreshing) {
    //   this.isRefreshing = true;
      this.refreshTokenSubject.next(null);
      const token = this.tokenService.getRefreshToken();
      if (token)
        return this.authService.refreshToken(token).pipe(
          switchMap((token: any) => {
            this.isRefreshing = false;
            this.tokenService.saveToken(token.accessToken);
            this.refreshTokenSubject.next(token.accessToken);

            return next.handle(this.addTokenHeader(request, token.accessToken));
          }),
          catchError((error) => {
            if (error.status == '400') {
              this.tokenService.signOut();
              // this.authService.logoutUser((token as any).id);

              this.router.navigate(['/auth/entranceType']).then(r => console.log('in redirect to login page'));
            }
            return throwError(error);
          })
        );
    // }
    return this.refreshTokenSubject.pipe(
      filter((token) => token !== null),
      take(1),
      switchMap((token) => next.handle(this.addTokenHeader(request, token)))
    );
  }
  private addTokenHeader(request: HttpRequest<any>, token: string) {
    /* for Spring Boot back-end */
    return request.clone({
      headers: request.headers
        .set(TOKEN_HEADER_KEY, 'Bearer ' + token)
        .set('Accept-Language', localStorage.getItem('lang') || 'bg'),
    });
    /* for Node.js Express back-end */
    // return request.clone({ headers: request.headers.set(TOKEN_HEADER_KEY, token) });
  }
}
export const authInterceptorProviders = [
  { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
];
