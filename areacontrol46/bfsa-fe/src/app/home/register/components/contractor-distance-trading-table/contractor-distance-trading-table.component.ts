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
import { ContractorDistanceTradingDialogComponent } from '../contractor-distance-trading-dialog/contractor-distance-trading-dialog.component';

@Component({
  selector: 'app-contractor-distance-trading-table',
  templateUrl: './contractor-distance-trading-table.component.html',
  styleUrls: ['./contractor-distance-trading-table.component.scss'],
})
export class ContractorDistanceTradingTableComponent
  implements OnInit, AfterViewInit
{
  constructor(private dialog: MatDialog, private _snackBar: MatSnackBar) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') options: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  displayedColumns: string[] = [
    'applicationNumber',
    'phone',
    'email',
    'website',
    'status',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();
  public dataSourceFilter = [
    {
      name: 'Регистър на бизнес операторите, които извършват търговия с храни от разстояние',
      code: '0002019',
    },
  ];

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
    this.dataSource.data = this.options;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(data: any) {
    const dialogRef = this.dialog.open(
      ContractorDistanceTradingDialogComponent,
      {
        width: '400px',
        data,
      }
    );

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Distance trading updated successfully', '', {
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
