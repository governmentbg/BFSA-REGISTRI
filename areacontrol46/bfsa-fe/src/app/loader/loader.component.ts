import { ChangeDetectorRef, Component } from '@angular/core';
import { LoaderService } from '../services/loader.service';

@Component({
  selector: 'app-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.scss'],
})
export class LoaderComponent {
  public isLoading: boolean;

  constructor(
    private loaderService: LoaderService,
    private cd: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loaderService.getLoaderStatus().subscribe((res) => {
      this.isLoading = res;
      this.cd.detectChanges();
    });
  }
}
