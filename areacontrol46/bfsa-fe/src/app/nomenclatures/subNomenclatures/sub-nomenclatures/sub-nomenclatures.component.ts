import { Component, Input, OnChanges, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTable } from '@angular/material/table';
import { AddNomenclatureComponent } from 'src/app/modals/add-nomenclature/add-nomenclature.component';
import { Nomenclature } from 'src/app/models/nomenclature';
import { NomenclatureService } from 'src/app/services/nomenclature.service';

@Component({
  selector: 'app-sub-nomenclatures',
  templateUrl: './sub-nomenclatures.component.html',
  styleUrls: ['./sub-nomenclatures.component.scss'],
})
export class SubNomenclaturesComponent implements OnChanges {
  @ViewChild(MatTable) table: MatTable<Nomenclature>;
  @Input()
  public dataSource: Nomenclature[];

  @Input()
  categoryCode: string;

  public displayedColumns = ['code', 'description', 'name', 'actions'];

  constructor(
    public dialog: MatDialog,
    private readonly nomenclaturesService: NomenclatureService
  ) {}

  ngOnInit(): void {}

  ngOnChanges() {
    if (this.dataSource && this.table) {
      this.table.renderRows();
    }
  }

  addData() {
    this.table.renderRows();
  }

  removeData() {
    this.dataSource.pop();
    this.table.renderRows();
  }

  editNomenclature(data: Nomenclature, index: number) {
    const code = data.code;
    const dialogRef = this.dialog.open(AddNomenclatureComponent, {
      height: '300px',
      width: '400px',
      data,
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.nomenclaturesService
          .editNomenclature(data.nomenclature, code)
          .subscribe((res) => {
            this.dataSource[index] = res;
            this.table.renderRows();
          });
      }
    });
  }

  addNomenclature() {
    const dialogRef = this.dialog.open(AddNomenclatureComponent, {
      height: '300px',
      width: '400px',
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.nomenclaturesService
          .addNomenclature(data.nomenclature, this.categoryCode)
          .subscribe((res) => {
            this.dataSource.push(res);
            this.table.renderRows()
          });
      }
    });
  }
}
