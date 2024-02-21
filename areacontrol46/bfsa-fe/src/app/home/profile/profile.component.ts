import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProfileService } from './services/profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss'],
})
export class ProfileComponent implements OnInit {
  profileForm = this.fb.group({
    theme: this.profileService.hasDarkTheme(),
  });
  user = this.profileService.getUser();

  constructor(
    private fb: UntypedFormBuilder,
    private profileService: ProfileService
  ) {}

  ngOnInit(): void {
    this.profileForm.controls['theme'].valueChanges.subscribe(
      (mode: string) => {
        this.profileService.setTheme(mode);
      }
    );
  }
}
