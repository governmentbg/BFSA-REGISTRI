import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { RouterModule } from '@angular/router';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import * as dayjs from 'dayjs';
import { applicationStatus } from 'src/app/enums/applicationStatus';
import { AdjuvantDialogComponent } from '../adjuvant-dialog/adjuvant-dialog.component';

@Component({
  selector: 'app-adjuvant-table',
  templateUrl: './adjuvant-table.component.html',
  styleUrls: ['./adjuvant-table.component.scss'],
})
export class AdjuvantTableComponent implements OnInit, AfterViewInit {
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') options: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  public displayedColumns: string[] = [
    'adjuvantName',
    'adjuvantProductFormulationTypeName',
    'manufacturerName',
    'supplierFullName',
    'orderNumber',
    'status',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();

  constructor(
    private readonly dialog: MatDialog,
    private readonly _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    console.log(this.options);
    this.dataSource.data = this.options;
  }

  formatDate(date?: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  edit(data: any) {
    const dialogRef = this.dialog.open(AdjuvantDialogComponent, {
      width: '400px',
      data,
    });
    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Certificates updated successfully', '', {
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

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }
}
