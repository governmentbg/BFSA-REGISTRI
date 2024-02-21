import { Component, OnInit } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { BuildInfoInterface } from './interfaces/build-info-interface';
import { DashboardService } from './services/dashboard.service';
import build from '../../../build';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {
  backendBuildInfo: BuildInfoInterface;
  frontendBuildInfo: typeof build;
  errorBuildInfo: string = '';

  constructor(
    private dashboardService: DashboardService,
    private title: Title,
    private translate: TranslateService
  ) {}

  ngOnInit(): void {
    this.frontendBuildInfo = build;
    this.translate.get('dashboard.title').subscribe((title) => {
      this.title.setTitle(title);
    });
    this.dashboardService.getBuildInfo().subscribe({
      next: (buildInfo: BuildInfoInterface) => {
        this.backendBuildInfo = buildInfo;
      },
      error: (err) => {
        this.errorBuildInfo = err;
      },
    });
  }
}
