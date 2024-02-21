import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-settlement',
  templateUrl: './settlement.component.html',
  styleUrls: ['./settlement.component.scss'],
})
export class SettlementComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('settlement.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
