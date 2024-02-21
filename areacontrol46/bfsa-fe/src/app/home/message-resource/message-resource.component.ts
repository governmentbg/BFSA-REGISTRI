import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import {TranslateService} from '@ngx-translate/core';

@Component({
  selector: 'app-message-resource',
  templateUrl: './message-resource.component.html',
  styleUrls: ['./message-resource.component.scss'],
})
export class MessageResourceComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('messageResource.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
