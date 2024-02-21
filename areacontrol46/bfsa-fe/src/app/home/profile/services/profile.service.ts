import { Injectable } from '@angular/core';
import { TokenStorageService } from 'src/app/services/token.service';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  constructor(private tokenStorage: TokenStorageService) {}

  hasDarkTheme() {
    return localStorage.getItem('theme') === 'dark' ? true : false;
  }

  setTheme(mode: string): void {
    localStorage.setItem('theme', mode ? 'dark' : 'light');
    document.body.classList.remove('light');
    document.body.classList.toggle('dark');
  }

  getUser() {
    return this.tokenStorage.getUser();
  }
}
