import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { BranchInterface } from '../../interfaces/branch-interface';
import { ContractorInterface } from '../../interfaces/contractor-interface';
import { ContractorService } from '../../services/contractor.service';
import { ContractorDialogComponent } from '../contractor-dialog/contractor-dialog.component';

@Component({
  selector: 'app-contractor-list',
  templateUrl: './contractor-list.component.html',
  styleUrls: ['./contractor-list.component.scss'],
})
export class ContractorListComponent implements OnInit, AfterViewInit {
  applicants: ContractorInterface[];
  roles: string[] = [];
  branches: BranchInterface[];
  pageSize: number = 10;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = [
    'enabled',
    // 'id',
    'email',
    'fullName',
    'username',
    'identifier',
    'roles',
    '*',
  ];
  dataSource = new MatTableDataSource();

  constructor(
    public dialog: MatDialog,
    private applicantService: ContractorService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.applicantService
      .getAllContractors()
      .subscribe((applicants: ContractorInterface[]) => {
        this.dataSource.data = applicants;
        this.applicants = applicants;
      });
    this.applicantService.getRoles().subscribe((roles: string[]) => {
      this.roles = roles;
    });
    this.applicantService
      .getBranches()
      .subscribe((branches: BranchInterface[]) => {
        this.branches = branches;
      });
  }

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  editContractor(applicant: ContractorInterface) {
    const dialogRef = this.dialog.open(ContractorDialogComponent, {
      width: '400px',
      data: {
        isAdd: false,
        applicant,
        roles: this.roles,
        branches: this.branches,
      },
    });

    dialogRef.afterClosed().subscribe((applicant: ContractorInterface) => {
      if (applicant) {
        this.applicantService
          .updateContractor(applicant.id, applicant)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open('Contractor updated successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the applicant: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }
}
