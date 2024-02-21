import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NomenclatureInterface } from '../../interfaces/nomenclature-interface';
import { NomenclatureService } from '../../services/nomenclature.service';
import { NomenclatureDialogComponent } from '../nomenclature-dialog/nomenclature-dialog.component';

@Component({
  selector: 'app-nomenclature-list',
  templateUrl: './nomenclature-list.component.html',
  styleUrls: ['./nomenclature-list.component.scss'],
})
export class NomenclatureListComponent implements OnInit {
  nomenclatures: NomenclatureInterface[];

  constructor(
    private nomenclatureService: NomenclatureService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.nomenclatureService
      .getNomenclatures()
      .subscribe((nomenclatures: NomenclatureInterface[]) => {
        console.log(nomenclatures);
        this.nomenclatures = nomenclatures;
      });
  }

  addNomenclature() {
    const dialogRef = this.dialog.open(NomenclatureDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((nomenclature: NomenclatureInterface) => {
      if (nomenclature) {
        console.log(nomenclature);
        this.nomenclatureService.addNomenclature(nomenclature).subscribe({
          next: (res) => {
            this.initialize();
            this._snackBar.open('Nomenclature added successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the nomenclature: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }
}
