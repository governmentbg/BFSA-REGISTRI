import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-language',
  templateUrl: './language.component.html',
  styleUrls: ['./language.component.scss'],
})
export class LanguageComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('language.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
