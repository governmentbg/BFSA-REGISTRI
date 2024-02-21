import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from 'src/app/services/user.service';
import { ApiResponse } from 'src/app/shared/interfaces/common.interface';

@Component({
  selector: 'app-verify-registration',
  templateUrl: './verify-registration.component.html',
  styleUrls: ['./verify-registration.component.scss'],
})
export class VerifyRegistrationComponent implements OnInit {
  public verifiedRegistration = false;
  public token: string;
  public lang: string;
  message?: string;

  constructor(
    private readonly userService: UserService,
    private router: ActivatedRoute
  ) {
    this.token = this.router.snapshot.queryParams["token"];
    this.lang = this.router.snapshot.queryParams["lang"];
    localStorage.setItem("lang", this.lang || "bg");
  }

  ngOnInit(): void {
    this.userService.verifyRegistration(this.token).subscribe({
      next: (success: ApiResponse) => {
        this.message = success.message
      },
      error: (e) => {
        console.error(e);
      },
    });
  }
}
