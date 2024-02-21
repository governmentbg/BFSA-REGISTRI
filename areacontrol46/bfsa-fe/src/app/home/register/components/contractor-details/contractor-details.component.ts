import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { catchError, map, mergeMap, of, tap } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { RegisterService } from '../../services/register.service';
import { DirectorateCode } from '../../interfaces/directorate-code';

interface ColumnInterface {
  columnDef: string;
  header: string;
}

@Component({
  selector: 'app-contractor-details',
  templateUrl: './contractor-details.component.html',
  styleUrls: ['./contractor-details.component.scss'],
})
export class ContractorDetailsComponent implements OnInit {
  public applicant: ContractorInterface;

  public directorateCode = DirectorateCode;
  public user: { roles: string[]; directorateCode: string } = JSON.parse(
    sessionStorage.getItem('auth-user') || '{}'
  );

  public facilities: any[];
  public fertilizers: {}[];
  public seeds: {}[];
  public vehicles: {}[];
  public pppFacilities: {}[];
  public papers: {}[];
  public distanceTrading: {}[];
  public foodAditives: {}[];
  public plantProtectionServices: {};
  public adjuvants: {}[];

  constructor(
    private title: Title,
    private translate: TranslateService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private registerService: RegisterService
  ) {}

  ngOnInit(): void {
    this.initialize();
    this.translate.get('registers.contractor.title').subscribe((title) => {
      this.title.setTitle(title);
    });
  }

  initialize(): void {
    this.activatedRoute.params
      .pipe(
        mergeMap((params: Params) => {
          return this.registerService.getContractor(params['id']).pipe(
            catchError((error) => {
              if (error.status === 404) {
                this.router.navigate(['app/dashboard']);
              }
              return of(error);
            })
          );
        }),
        tap((applicant: ContractorInterface) => {
          this.applicant = applicant;
          this.getFacilities();
        })
      )
      .subscribe();
  }

  tabChanged(event: MatTabChangeEvent) {
    const tabLabel = event.tab?.textLabel;
    switch (tabLabel) {
      case 'Обекти':
        this.getFacilities();
        break;
      case 'Търговия от разстояние':
        this.getDistanceTrading();
        break;
      case 'Растителнозащитни услуги':
        this.getPlantProtectionServices();
        break;
      case 'Автомобили':
        this.getVehicles();
        break;
      case 'Сертификати/Разрешения':
        this.getPapers();
        break;
      case 'Хранителни добавки':
        this.getFoodAdditives();
        break;
      case 'Адюванти':
        this.getAdjuvants();
        break;
      case 'Торове':
        this.getFertilizers();
        break;
      case 'Семена':
        this.getSeeds();
        break;
      default:
        console.log(`Sorry, we are out of ${tabLabel}.`);
    }
  }

  getFoodAdditives() {
    this.registerService
      .getFoodAdditives(this.applicant.id)
      .pipe(
        tap((res) => {
          console.log(res.content);
          this.foodAditives = res.content;
        })
      )
      .subscribe();
  }

  getAdjuvants() {
    this.registerService
      .getAdjuvants(this.applicant.id)
      .subscribe((res) => (this.adjuvants = res));
  }

  getPlantProtectionServices() {
    this.registerService
      .getPlantProtectionServices(this.applicant.id)
      .subscribe((res) => {
        console.log(res);
        // do data manipulation into one array here
        this.plantProtectionServices = res;
      });
  }

  getDistanceTrading() {
    this.registerService
      .getDistanceTradingForContractor(this.applicant.id)
      .pipe(map((res) => res.results))
      .subscribe((res) => {
        this.distanceTrading = res;
      });
  }

  getPapers() {
    this.registerService
      .getContractorPapers(this.applicant.id)
      .subscribe((res) => {
        console.log(res);
        this.papers = res;
      });
  }

  getVehicles() {
    this.registerService
      .getVehiclesForContractor(this.applicant.id)
      .subscribe((res) => (this.vehicles = res));
  }

  getFacilities() {
    this.registerService
      .getContractorFacilities(this.applicant.id)
      .subscribe((res) => {
        console.log(res);
        this.facilities = res;
      });
  }

  getFertilizers() {
    this.registerService.getFertilizers(this.applicant.id).subscribe((res) => {
      console.log(res);
      this.fertilizers = res;
    });
  }

  getSeeds() {
    this.registerService.getSeeds(this.applicant.id).subscribe((res) => {
      console.log(res);
      this.seeds = res;
    });
  }

  get isAdmin() {
    return this.user.roles?.includes('ROLE_ADMIN');
  }

  get isExpertPhytosanitary() {
    return (
      this.user.directorateCode ===
      this.directorateCode
        .CLASSIFIER_REGISTER_PHYTOSANITARY_CONTROL_DIRECTORATE_CODE
    );
  }
}
