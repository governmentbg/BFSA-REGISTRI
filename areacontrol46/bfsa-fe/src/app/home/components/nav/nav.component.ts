import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { TokenStorageService } from 'src/app/services/token.service';
import { AuthenticationService } from '../../../services/auth.service';

@Component({
  selector: 'app-nav',
  templateUrl: './nav.component.html',
  styleUrls: ['./nav.component.scss'],
})
export class NavComponent {
  public userToken = this.tokenStorage.getUser();

  isHandset$: Observable<boolean> = this.breakpointObserver
    .observe(Breakpoints.Handset)
    .pipe(map((result) => result.matches));

  constructor(
    private breakpointObserver: BreakpointObserver,
    private authenticationService: AuthenticationService,
    private router: Router,
    private tokenStorage: TokenStorageService
  ) {}

  ngOnInit() {}

  logout() {
    const id = this.userToken.userId;
    if (this.userToken.type === '0') {
      this.authenticationService.logoutUser(id).subscribe((res) => {});
      this.router.navigate(['/auth/login']);
    } else if (this.userToken.type === '1') {
      this.authenticationService.logoutContractor(id).subscribe((res) => {});
      this.router.navigate(['/auth/signin']);
    }
    this.tokenStorage.signOut();
  }

  public get isAdmin() {
    return this.userToken?.roles?.includes('ROLE_ADMIN');
  }
}
