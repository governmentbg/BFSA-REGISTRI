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

@Component({
  selector: 'app-contractor-ppp-facilities-table',
  templateUrl: './contractor-ppp-facilities-table.component.html',
  styleUrls: ['./contractor-ppp-facilities-table.component.scss'],
})
export class ContractorPppFacilitiesTableComponent
  implements OnInit, AfterViewInit
{
  constructor(private _snackBar: MatSnackBar, private dialog: MatDialog) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') options: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  public displayedColumns: string[] = [
    'legalActNumber',
    'legalActType',
    'objectType',
    'address',
    'person',
    'identifier',
    'status',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();

  ngOnInit(): void {
    this.initialize();

    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
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

  edit(item: any) {
    const dialogRef = this.dialog.open(ViewS3180DialogComponent, {
      width: '400px',
      data: { item },
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Facility updated successfully', '', {
          duration: 1000,
        });
      }
    });
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
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
