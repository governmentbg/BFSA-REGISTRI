import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-activity-group',
  templateUrl: './activity-group.component.html',
  styleUrls: ['./activity-group.component.scss'],
})
export class ActivityGroupComponent implements OnInit {
  constructor(private title: Title, private translate: TranslateService) {}

  ngOnInit(): void {
    this.translate.get('activityGroup.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }
}
