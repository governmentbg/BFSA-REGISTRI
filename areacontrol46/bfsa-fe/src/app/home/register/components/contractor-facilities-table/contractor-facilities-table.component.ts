import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { ViewS3180DialogComponent } from '../view-s3180-dialog/view-s3180-dialog.component';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { FacilityStatus } from '../../interfaces/facility-status';
import { RegisterService } from '../../services/register.service';
import { ViewS3182DialogComponent } from '../view-s3182-dialog/view-s3182-dialog.component';

@Component({
  selector: 'app-contractor-facilities-table',
  templateUrl: './contractor-facilities-table.component.html',
  styleUrls: ['./contractor-facilities-table.component.scss'],
})
export class ContractorFacilitiesTableComponent
  implements OnInit, AfterViewInit
{
  constructor(
    private dialog: MatDialog,
    private _snackBar: MatSnackBar,
    private readonly registerService: RegisterService
  ) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') facilities: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  public displayedColumns: string[] = [
    'regNumber',
    'facilityPaperRegNumbers',
    'fullAddress',
    'activityTypeName',
    'facilityTypeName',
    'serviceType',
    'status',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();
  public dataSourceFilter = [
    {
      name: 'Регистър на одобрените обекти за производство, преработка и/или дистрибуция на храни от животински произход',
      code: '0002004',
    },
    {
      name: 'Млекосъбирателни пунктове и помещения за съхранение на мляко',
      code: '0002005',
    },
    {
      name: 'Митнически складове',
      code: '0002007',
    },
    {
      name: 'Регистър на обекти за производство и търговия на едро с материали и предмети, предназначени за контакт с храни',
      code: '0002016',
    },
    {
      name: 'Регистър на лицата, които получават пратки храни от животински произход от ЕС',
      code: '0002022',
    },
    {
      name: 'Регистър на издадените разрешения за оператори на хранителни банки',
      code: '0002023',
    },
    {
      name: 'Регистър на обекти за обществено хранене (ОН)',
      code: '0002011',
    },
    {
      name: 'Списък на одобрените предприятия за храни от животински произход, които не са обект на Приложение ІІІ на Регламент 853/2004/ЕЕС',
      code: '0002006',
    },
    {
      name: 'Регистър на регистрирани обекти за производство, преработка и/или дистрибуция на храни от неживотински произход',
      code: '0002008',
    },
    {
      name: 'Наематели - търговци',
      code: '0002012',
    },
    {
      name: 'Регистър на обекти за търговия на дребно',
      code: '0002010',
    },
  ];

  ngOnInit(): void {
    this.initialize();

    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
        const filteredOptions = this.facilities.filter((facility) => {
          for (let i = 0; i < facility.registers.length; i++) {
            if (facility.registers[i].code === code) {
              return true;
            }
          }
          return false;
        });
        console.log(filteredOptions);
        this.dataSource.data = filteredOptions;
      }
    });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  initialize(): void {
    this.dataSource.data = this.facilities;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(data: any, id: string) {
    const serviceType = data.serviceType;
    let dialogRef;

    console.log(serviceType);
    this.registerService.getFacilityInfoById(id).subscribe((data) => {
      switch (serviceType) {
        case 'S3180':
          dialogRef = this.dialog.open(ViewS3180DialogComponent, {
            data,
            panelClass: 'custom-dialog-container',
          });
          dialogRef.afterClosed().subscribe((res) => {
            if (res) {
              console.log(res);
              // this.dataSource[index] = res;
              this.initialize();
              this._snackBar.open('Facility updated successfully', '', {
                duration: 1000,
              });
            }
          });
          break;
        case 'S3182':
          dialogRef = this.dialog.open(ViewS3182DialogComponent, {
            data,
            panelClass: 'custom-dialog-container',
          });
          dialogRef.afterClosed().subscribe((res) => {
            if (res) {
              console.log(res);
              // this.dataSource[index] = res;
              this.initialize();
              this._snackBar.open('Facility updated successfully', '', {
                duration: 1000,
              });
            }
          });
          break;
      }
    });
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.facilities;
    event.stopPropagation();
  }

  translateFacilityStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(FacilityStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }
}
