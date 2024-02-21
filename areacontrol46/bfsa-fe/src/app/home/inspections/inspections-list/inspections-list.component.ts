import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { Router } from '@angular/router';
import { InspectionInterface } from '../interfaces/inspection-interface';
import { InspectionService } from '../services/inspection.service';
import { InspectionType } from '../interfaces/inspection-type';

@Component({
  selector: 'app-inspections-list',
  templateUrl: './inspections-list.component.html',
  styleUrls: ['./inspections-list.component.scss'],
})
export class InspectionsListComponent implements OnInit, AfterViewInit {
  inspections: InspectionInterface[];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  pageSize: number = 10;

  displayedColumns: string[] = [
    'id',
    'applicantFullName',
    'applicantIdentifier',
    'description',
    'endDate',
    'inspectionType',
    'action',
  ];
  dataSource = new MatTableDataSource();

  constructor(
    private inspectionService: InspectionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.inspectionService
      .getInspections()
      .subscribe((inspections: InspectionInterface[]) => {
        this.dataSource.data = inspections;
        this.inspections = inspections;
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  edit(element: InspectionInterface) {
    this.router.navigate([`/app/inspections/${element.id}`, element]);
  }

  add() {
    this.router.navigate(['/app/inspections/new']);
  }

  translateInspection(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(InspectionType);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }
}
