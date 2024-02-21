import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorPlantProtectionServicesDialogComponent } from '../contractor-plant-protection-services-dialog/contractor-plant-protection-services-dialog.component';
import { ViewConsultantsDialogComponent } from '../view-consultants-dialog/view-consultants-dialog.component';
import { FormControl } from '@angular/forms';
import { applicationStatus } from 'src/app/enums/applicationStatus';
@Component({
  selector: 'app-contractor-plant-protection-services-table',
  templateUrl: './contractor-plant-protection-services-table.component.html',
  styleUrls: ['./contractor-plant-protection-services-table.component.scss'],
})
export class ContractorPlantProtectionServicesTableComponent
  implements OnInit, AfterViewInit
{
  constructor(
    public dialog: MatDialog,
    private readonly _snackBar: MatSnackBar
  ) {}

  @ViewChild('paginator') paginator: MatPaginator;

  @Input('options') data: any;
  @Input('applicant') applicant: ContractorInterface;
  displayedColumns: string[] = [
    'notificationNumber',
    'fullAddress',
    'address',
    'ch83CertifiedPerson',
    'status',
    'serviceType',
    'action',
  ];

  public registerControl = new FormControl('');
  public pageSize: number = 10;
  public dataSource = new MatTableDataSource();
  public dataSourceFilter = [
    {
      name: 'Фумигация на растения, растителни продукти и други обекти - S1590 ',
      serviceType: 'S1590',
    },
    {
      name: 'Третиране с продукти за растителна защита на семена за посев - S2699 ',
      serviceType: 'S2699',
    },
    {
      name: 'Консултантски услуги за интегрирано управление на вредители - S2700',
      serviceType: 'S2700',
    },
  ];
  ngOnInit(): void {
    this.initialize();
    //todo finish thiss
    this.registerControl.valueChanges.subscribe((serviceType) => {
      if (serviceType) {
        console.log(serviceType);
        const filteredOptions = this.data.filter(
          (el: any) => el.serviceType === serviceType
        );
        console.log(filteredOptions);
        this.dataSource.data = filteredOptions;
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  initialize(): void {
    this.dataSource.data = this.data;
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.data;
    event.stopPropagation();
  }

  translateApprovalDocumentStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(applicationStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }

  edit(data: any) {
    const dialogRef = this.dialog.open(
      ContractorPlantProtectionServicesDialogComponent,
      {
        width: '400px',
        data,
      }
    );

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open(
          'Plant protection services updated successfully',
          '',
          {
            duration: 1000,
          }
        );
      }
    });
  }

  viewConsultants(consultants: ContractorInterface[]) {
    this.dialog.open(ViewConsultantsDialogComponent, {
      width: '1500px',
      height: '900px',
      data: consultants,
    });
  }
}
