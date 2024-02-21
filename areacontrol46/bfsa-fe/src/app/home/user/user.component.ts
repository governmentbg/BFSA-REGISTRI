import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss'],
})
export class UserComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('user.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
