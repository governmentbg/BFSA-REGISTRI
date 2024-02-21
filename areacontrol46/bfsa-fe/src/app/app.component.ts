import { Component } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  constructor(public translate: TranslateService) {
    document.body.classList.add(localStorage.getItem('theme') || 'dark');
    this.translate.setDefaultLang(localStorage.getItem('lang') || 'bg');
  }
}
