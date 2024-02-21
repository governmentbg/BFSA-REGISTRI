import { Component, Input, OnInit, ViewChild } from '@angular/core';
import {} from '@angular/common';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { PermissionsDialogComponent } from '../permissions-dialog/permissions-dialog.component';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import * as dayjs from 'dayjs';
import { approvalDocumentStatus } from 'src/app/enums/approvalDocumentStatus.bg';

@Component({
  selector: 'app-permissions-table',
  templateUrl: './permissions-table.component.html',
  styleUrls: ['./permissions-table.component.scss'],
})
export class PermissionsComponentTable implements OnInit {
  constructor(private _snackBar: MatSnackBar, private dialog: MatDialog) {}
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
  ];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') data: any = [];
  @Input('applicant') applicant: ContractorInterface;
  displayedColumns: string[] = [
    'approvalDocumentNumber',
    'firstEntryDate',
    'approvalDocumentStatus',
    'action',
  ];

  pageSize: number = 10;
  registerControl = new FormControl('');
  dataSource = new MatTableDataSource();

  ngOnInit(): void {
    this.initialize();

    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
        const filteredOptions = this.data.filter(
          (item: any) => item.registerCode === code
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

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(item: any) {
    const dialogRef = this.dialog.open(PermissionsDialogComponent, {
      width: '400px',
      data: { item },
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        console.log(res);
        this._snackBar.open('Distance trading updated successfully', '', {
          duration: 1000,
        });
      }
    });
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

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.data;
    event.stopPropagation();
  }
}
