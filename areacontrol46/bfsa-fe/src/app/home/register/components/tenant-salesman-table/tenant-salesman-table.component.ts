import { Component, Input, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { applicationStatus } from 'src/app/enums/applicationStatus';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorSeedsDialogComponent } from '../contractor-seeds-dialog/contractor-seeds-dialog.component';

@Component({
  selector: 'app-tenant-salesman-table',
  templateUrl: './tenant-salesman-table.component.html',
  styleUrls: ['./tenant-salesman-table.component.scss'],
})
export class TenantSalesmanTableComponent {
  public displayedColumns: string[] = [
    'regNumber',
    'facilityRegNumber',
    'address',
    'identifier',
    'tenantName',
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
