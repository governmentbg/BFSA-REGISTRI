import { Component, inject, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-register-page',
  templateUrl: './register-page.component.html',
  styleUrls: ['./register-page.component.scss'],
})
export class RegisterPageComponent implements OnInit {
  constructor(
    public title: Title,
    private readonly translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.translate.get('registers.contractor.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
