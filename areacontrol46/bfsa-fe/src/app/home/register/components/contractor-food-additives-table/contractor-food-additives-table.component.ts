import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import {} from '@angular/material/input';
import { MatPaginator } from '@angular/material/paginator';
import {} from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { FacilityStatus } from '../../interfaces/facility-status';
import { ContractorFoodAdditivesDialogComponent } from '../contractor-food-additives-dialog/contractor-food-additives-dialog.component';
import { FoodSupplementStatus } from '../../interfaces/food-supplement-status';

@Component({
  selector: 'app-contractor-food-additives-table',
  templateUrl: './contractor-food-additives-table.component.html',
  styleUrls: ['./contractor-food-additives-table.component.scss'],
})
export class ContractorFoodAdditivesTableComponent
  implements OnInit, AfterViewInit
{
  constructor(private dialog: MatDialog, private _snackBar: MatSnackBar) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') foodAditives: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  public displayedColumns: string[] = [
    'regNumber',
    'name',
    'foodSupplementTypeName',
    'ingredients',
    'purpose',
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
  ];

  ngOnInit(): void {
    this.initialize();
    console.log(this.foodAditives);
    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
        const filteredOptions = this.foodAditives.filter((food) => {
          for (let i = 0; i < food.registers.length; i++) {
            if (food.registers[i].code === code) {
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

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(FoodSupplementStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }

  initialize(): void {
    this.dataSource.data = this.foodAditives;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(item: any) {
    const dialogRef = this.dialog.open(ContractorFoodAdditivesDialogComponent, {
      width: '400px',
      data: { item, facilityStatusList: FacilityStatus },
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Food additives updated successfully', '', {
          duration: 1000,
        });
      }
    });
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.foodAditives;
    event.stopPropagation();
  }
}
