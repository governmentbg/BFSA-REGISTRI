import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { UserService } from 'src/app/home/user/services/user.service';
import { BranchInterface } from '../../interfaces/branch-interface';
import { BranchService } from '../../services/branch.service';
import { BranchDialogComponent } from '../branch-dialog/branch-dialog.component';

@Component({
  selector: 'app-branch-list',
  templateUrl: './branch-list.component.html',
  styleUrls: ['./branch-list.component.scss'],
})
export class BranchListComponent implements OnInit, AfterViewInit {
  branches: BranchInterface[];
  pageSize: number = 10;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  displayedColumns: string[] = [
    'enabled',
    'name',
    // 'id',
    'address',
    'email',
    'description',
    'phone1',
    'phone2',
    'phone3',
    'main',
    'users',
    'identifier',
    'settlementCode',
    '*',
  ];
  dataSource = new MatTableDataSource();

  constructor(
    public dialog: MatDialog,
    private branchService: BranchService,
    private userService: UserService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.branchService
      .getBranches()
      .subscribe((branches: BranchInterface[]) => {
        this.dataSource.data = branches;
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

  editBranch(branch: BranchInterface) {
    const dialogRef = this.dialog.open(BranchDialogComponent, {
      width: '400px',
      data: { isAdd: false, branch },
    });

    dialogRef.afterClosed().subscribe((branch: BranchInterface) => {
      if (branch) {
        this.branchService.updateBranch(branch.id, branch).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open('Branch updated successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while updating the branch: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }

  addBranch() {
    const dialogRef = this.dialog.open(BranchDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });

    dialogRef.afterClosed().subscribe((branch: BranchInterface) => {
      if (branch) {
        this.branchService.addBranch(branch).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open('Branch added successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the branch: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }
}
