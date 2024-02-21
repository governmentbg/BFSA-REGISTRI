import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-nomenclature',
  templateUrl: './nomenclature.component.html',
  styleUrls: ['./nomenclature.component.scss'],
})
export class NomenclatureComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('nomenclature.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
