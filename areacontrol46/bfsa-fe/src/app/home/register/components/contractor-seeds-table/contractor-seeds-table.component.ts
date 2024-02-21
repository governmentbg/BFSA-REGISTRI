import { Component, Input, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { applicationStatus } from 'src/app/enums/applicationStatus';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorSeedsDialogComponent } from '../contractor-seeds-dialog/contractor-seeds-dialog.component';

@Component({
  selector: 'app-contractor-seeds-table',
  templateUrl: './contractor-seeds-table.component.html',
  styleUrls: ['./contractor-seeds-table.component.scss'],
})
export class ContractorSeedsTableComponent {
  public displayedColumns: string[] = [
    'regNumber',
    'regDate',
    'seedName',
    'seedQuantity',
    'seedTradeName',
    'applicationStatus',
    'action',
  ];
  public dataSource = new MatTableDataSource();

  constructor(private dialog: MatDialog, private _snackBar: MatSnackBar) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') seeds: any[] = [];
  @Input('applicant') applicant: ContractorInterface;

  ngOnChanges() {
    console.log(this.seeds);
    this.dataSource.data = this.seeds;
  }

  edit(data: any) {
    const dialogRef = this.dialog.open(ContractorSeedsDialogComponent, {
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
