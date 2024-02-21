import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { FacilityStatus } from '../../interfaces/facility-status';
import { ContractorCertificates83DialogComponent } from '../contractor-certificates83-dialog/contractor-certificates83-dialog.component';
import { approvalDocumentStatus } from 'src/app/enums/approvalDocumentStatus.bg';
import * as dayjs from 'dayjs';

@Component({
  selector: 'app-contractor-certificates83-table',
  templateUrl: './contractor-certificates83-table.component.html',
  styleUrls: ['./contractor-certificates83-table.component.scss'],
})
export class ContractorCertificates83TableComponent
  implements OnInit, AfterViewInit
{
  public dataSourceFilter = [
    {
      name: 'Национален електронен регистър на лицата, които притежават удостоверение за търговия с продукти за растителна защита, и на съответните обекти за търговия с продукти за растителна защита',
      code: '0002032',
    },
    {
      name: 'Национален електронен регистър на лицата, които притежават удостоверение за преопаковане на продукти за растителна защита, и на съответните обекти за преопаковане на продукти за растителна защита',
      code: '0002033',
    },
    {
      name: 'Регистър на лицата по чл. 6, ал. 1, т. 12 от ЗЗР, на които се издава временно разрешение за въвеждане, движение, притежаване и размножаване на карантинни вредители, растения, растителни продукти и други обекти за официални изпитвания, научноизследователски или образователни цели, сортов подбор и селекция[5]',
      code: '0002043',
    },
    {
      name: 'Национален електронен регистър на лицата, които притежават сертификат по чл. 83 ЗЗР',
      code: '0002041',
    },
    {
      name: 'Регистър на официалните бази на БАБХ и одобрените бази за биологично изпитване на ПРЗ',
      code: '0002029',
    },
    {
      name: 'Регистър на лицата, които притежават удостоверение за внос или въвеждане на партида от неодобрени активни вещества',
      code: '0002035',
    },
    {
      name: 'Национален електронен регистър на издадените разрешения за прилагане на продукти за растителна защита чрез въздушно пръскане',
      code: '0002036',
    },
    {
      name: 'Регистър на професионалните оператори - лицата по чл. 6, ал. 1, т. 11 от ЗЗР',
      code: '0002041',
    },
  ];

  constructor(
    private readonly dialog: MatDialog,
    private readonly _snackBar: MatSnackBar
  ) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') options: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  public displayedColumns: string[] = [
    'regNumber',
    'regDate',
    'validUntilDate',
    'status',
    'serviceType',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();

  formatDate(date?: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  ngOnInit(): void {
    this.initialize();

    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
        const filteredOptions = this.options.filter(
          (item) => item.registerCode === code
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
    console.log(this.options);
    this.dataSource.data = this.options;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(approvalDocumentStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }

  edit(item: any) {
    const dialogRef = this.dialog.open(
      ContractorCertificates83DialogComponent,
      {
        width: '400px',
        data: { item, facilityStatusList: FacilityStatus },
      }
    );

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Certificates updated successfully', '', {
          duration: 1000,
        });
      }
    });
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.options;
    event.stopPropagation();
  }
}
