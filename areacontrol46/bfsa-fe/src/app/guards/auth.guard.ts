import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';
import { TokenStorageService } from '../services/token.service';
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private readonly tokenStorageService: TokenStorageService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const user = this.tokenStorageService.getUser();
    if (user.token) {
      // logged in so return true
      return true;
    } else {
      // not logged in so redirect to login page with the return url
      if (user.type == '0') {
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl: state.url },
        });
      } else if (user.type == '1') {
        this.router.navigate(['/auth/signin'], {
          queryParams: { returnUrl: state.url },
        });
      } else {
        this.router.navigate(['/auth/entranceType'], {
          queryParams: { returnUrl: state.url },
        });
      }
      
      return false;
    }
  }
}
