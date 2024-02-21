import { AfterViewInit, Component, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { BranchInterface } from '../../interfaces/branch-interface';
import { UserInterface } from '../../interfaces/user-interface';
import { UserService } from '../../services/user.service';
import { UserDialogComponent } from '../user-dialog/user-dialog.component';
import { TokenStorageService } from '../../../../services/token.service';
import { DirectorateCode } from '../../interfaces/directorate-code';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
})
export class UserListComponent implements OnInit, AfterViewInit {
  users: UserInterface[];
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
    'directorateCode',
    '*',
  ];
  dataSource = new MatTableDataSource();

  constructor(
    public dialog: MatDialog,
    private userService: UserService,
    private _snackBar: MatSnackBar,
    private tokenService: TokenStorageService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.userService.getUsers().subscribe((users: UserInterface[]) => {
      this.dataSource.data = users;
      this.users = users;
    });
    this.userService.getRoles().subscribe((roles: string[]) => {
      this.roles = roles;
    });
    this.userService.getBranches().subscribe((branches: BranchInterface[]) => {
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

  editUser(user: UserInterface) {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '400px',
      data: {
        isAdd: false,
        user,
        roles: this.roles,
        branches: this.branches,
        directorateCodeList: DirectorateCode,
      },
    });

    dialogRef.afterClosed().subscribe((user: UserInterface) => {
      if (user) {
        this.userService.updateUser(user.id, user).subscribe({
          next: () => {
            const currentUser = this.tokenService.getUser();

            if (user.id === currentUser.userId) {
              currentUser.branchId = user.branchId;
              this.tokenService.saveUser(currentUser);
            }

            this.initialize();
            this._snackBar.open('User updated successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while updating the user: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }

  addUser() {
    const dialogRef = this.dialog.open(UserDialogComponent, {
      width: '400px',
      data: { isAdd: true, roles: this.roles, branches: this.branches },
    });

    dialogRef.afterClosed().subscribe((user: UserInterface) => {
      if (user) {
        this.userService.addUser(user).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open('User added successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the user: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }
}
