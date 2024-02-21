import {
  AfterViewInit,
  Component,
  inject,
  Input,
  OnInit,
  ViewChild,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule, MatPaginator } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule, MatTableDataSource } from '@angular/material/table';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorVehiclesDialogComponent } from '../contractor-vehicles-dialog/contractor-vehicles-dialog.component';
import { RouterModule } from '@angular/router';
import { VehicleStatus } from '../../interfaces/vehicle-status';

@Component({
  selector: 'app-contractor-vehicles-table',
  templateUrl: './contractor-vehicles-table.component.html',
  styleUrls: ['./contractor-vehicles-table.component.scss'],
})
export class ContractorVehiclesTableComponent implements OnInit, AfterViewInit {
  constructor(private dialog: MatDialog, private _snackBar: MatSnackBar) {}

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @Input('options') vehicles: any[] = [];
  @Input('applicant') applicant: ContractorInterface;
  displayedColumns: string[] = [
    'certificateNumber',
    'registrationPlate',
    'vehicleTypeName',
    'name',
    'load',
    'entryDate',
    'status',
    'action',
  ];

  public pageSize: number = 10;
  public registerControl = new FormControl('');
  public dataSource = new MatTableDataSource();
  public dataSourceFilter = [
    {
      name: 'Национален регистър на моторните превозни средства за транспорт на храни',
      code: '0002018',
    },
  ];

  ngOnInit(): void {
    this.initialize();

    this.registerControl.valueChanges.subscribe((code) => {
      if (code) {
        console.log(code);
        const filteredOptions = this.vehicles.filter((vehicle) => {
          for (let i = 0; i < vehicle.registers.length; i++) {
            if (vehicle.registers[i].code === code) {
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
    this.dataSource.data = this.vehicles;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(item: any, index: number) {
    const dialogRef = this.dialog.open(ContractorVehiclesDialogComponent, {
      width: '400px',
      data: { item, vehicleStatusList: VehicleStatus },
    });

    dialogRef.afterClosed().subscribe((res) => {
      if (res) {
        this.dataSource.data[index] = res;
        this.initialize();
        console.log(res);
        this._snackBar.open('Vehicle updated successfully', '', {
          duration: 1000,
        });
      }
    });
  }

  clearSelection(event: Event) {
    this.registerControl.setValue(null);
    this.dataSource.data = this.vehicles;
    event.stopPropagation();
  }

  translateVehicleStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(VehicleStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg' && index !== -1) {
      return statuses[index][1];
    }
    return status;
  }
}
