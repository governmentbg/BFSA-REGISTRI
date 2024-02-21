import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Params } from '@angular/router';
import { NomenclatureInterface } from '../../interfaces/nomenclature-interface';
import { NomenclatureDialogComponent } from '../nomenclature-dialog/nomenclature-dialog.component';
import { Location } from '@angular/common';
import { NomenclatureService } from '../../services/nomenclature.service';

@Component({
  selector: 'app-nomenclature-item',
  templateUrl: './nomenclature-item.component.html',
  styleUrls: ['./nomenclature-item.component.scss'],
})
export class NomenclatureItemComponent implements OnInit {
  nomenclature: NomenclatureInterface;

  constructor(
    private nomenclatureService: NomenclatureService,
    private activatedRoute: ActivatedRoute,
    private location: Location,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const code = params['code'];
      this.nomenclatureService
        .getNomenclature(code)
        .subscribe((nomenclature: NomenclatureInterface) => {
          this.nomenclature = nomenclature;
        });
    });
  }

  back(): void {
    this.location.back();
  }

  editNomenclature(nomenclature: NomenclatureInterface) {
    const dialogRef = this.dialog.open(NomenclatureDialogComponent, {
      width: '450px',
      data: { isAdd: false, nomenclature },
    });
    dialogRef.afterClosed().subscribe((nomenclature: NomenclatureInterface,) => { 
      if (nomenclature) {
        console.log(nomenclature);
        this.nomenclatureService
          .updateNomenclature(nomenclature.code, nomenclature)
          .subscribe({
            next: (res) => {
              this._snackBar.open('Nomenclature updated successfully', '', {
                duration: 1000,
              });
              this.initialize();
            },
            error: (err) => {
              this._snackBar.open(
                `Error while updating the nomenclature: ${err.message}`,
                'Close'
              );
            },
          });
      }
    });
  }

  addSubNomenclature() {
    const dialogRef = this.dialog.open(NomenclatureDialogComponent, {
      width: '450px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((nomenclature: NomenclatureInterface) => {
      if (nomenclature) {
        console.log(nomenclature);
        this.nomenclatureService
          .addSubNomenclature(this.nomenclature.code, nomenclature)
          .subscribe({
            next: (res) => {
              this._snackBar.open('Nomenclature added successfully', '', {
                duration: 1000,
              });
              this.initialize();
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
