import { Component, Input, OnChanges, ViewChild } from '@angular/core';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { FoodSupplementStatus } from '../../interfaces/food-supplement-status';
import { applicationStatus } from 'src/app/enums/applicationStatus';
import { ContractorFertilizersDialogComponent } from '../contractor-fertilizers-dialog/contractor-fertilizers-dialog.component';

@Component({
  selector: 'app-contractor-fertilizers-table',
  templateUrl: './contractor-fertilizers-table.component.html',
  styleUrls: ['./contractor-fertilizers-table.component.scss'],
})
export class ContractorFertilizersTableComponent implements OnChanges {
  public displayedColumns: string[] = [
    'regNumber',
    'regDate',
    'name',
    'productCategoryName',
    'productTypeName',
    'manufacturerFullName',
    'applicationStatus',
    'action',
  ];
  public dataSource = new MatTableDataSource();

  constructor(private dialog: MatDialog, private _snackBar: MatSnackBar) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') fertilizers: any[] = [];
  @Input('applicant') applicant: ContractorInterface;

  ngOnChanges() {
    console.log(this.fertilizers);
    this.dataSource.data = this.fertilizers;
  }

  edit(data: any) {
    const dialogRef = this.dialog.open(ContractorFertilizersDialogComponent, {
      width: '400px',
      data,
    });
    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Ferilizer updated successfully', '', {
          duration: 1000,
        });
      }
    });
  }

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(applicationStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }
}
