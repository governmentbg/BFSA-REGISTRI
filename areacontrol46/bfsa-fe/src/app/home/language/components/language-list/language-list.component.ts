import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { LanguageInterface } from '../../interfaces/language-interface';
import { LanguageService } from '../../services/language.service';
import { LanguageDialogComponent } from '../language-dialog/language-dialog.component';

@Component({
  selector: 'app-language-list',
  templateUrl: './language-list.component.html',
  styleUrls: ['./language-list.component.scss'],
})
export class LanguageListComponent implements OnInit {
  languages: LanguageInterface[];

  displayedColumns: string[] = [
    'enabled',
    'languageId',
    'name',
    'locale',
    'main',
    'description',
    '*',
  ];
  dataSource = new MatTableDataSource();

  constructor(
    public dialog: MatDialog,
    private languageService: LanguageService,
    private _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.languageService.getLanguages().subscribe((languages) => {
      this.dataSource.data = languages;
      this.languages = languages;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  editLanguage(language: LanguageInterface) {
    const dialogRef = this.dialog.open(LanguageDialogComponent, {
      width: '400px',
      data: { isAdd: false, language },
    });

    dialogRef.afterClosed().subscribe((language: LanguageInterface) => {
      if (language) {
        this.languageService
          .updateLanguage(language.languageId, language)
          .subscribe({
            next: () => {
              this.initialize();
              this._snackBar.open('Language updated successfully', '', {
                duration: 1000,
              });
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the language: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }

  addLanguage() {
    const dialogRef = this.dialog.open(LanguageDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });

    dialogRef.afterClosed().subscribe((language: LanguageInterface) => {
      if (language) {
        this.languageService.addLanguage(language).subscribe({
          next: () => {
            this.initialize();
            this._snackBar.open('Language added successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the language: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }
}
