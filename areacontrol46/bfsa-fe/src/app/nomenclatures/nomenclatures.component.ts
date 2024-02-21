import {
  trigger,
  state,
  style,
  transition,
  animate,
} from '@angular/animations';
import {
  ChangeDetectionStrategy,
  Component,
  OnInit,
  ViewChild,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTable } from '@angular/material/table';
import { NomenclatureService } from 'src/app/services/nomenclature.service';
import { AddNomenclatureComponent } from '../modals/add-nomenclature/add-nomenclature.component';
import { Nomenclature } from '../models/nomenclature';

@Component({
  selector: 'app-nomenclatures',
  templateUrl: './nomenclatures.component.html',
  styleUrls: ['./nomenclatures.component.scss'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0' })),
      state('expanded', style({ height: '*' })),
      transition(
        'expanded <=> collapsed',
        animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')
      ),
    ]),
  ],
})
export class NomenclaturesComponent implements OnInit {
  matTableDataSource: Nomenclature[];
  @ViewChild(MatTable) table: MatTable<Nomenclature[]>;

  public categoryIndex: number 
  public renderRows: any;
  public nomeclaturesChildren: Nomenclature[];
  public columnsToDisplay = ['code', 'description', 'name'];
  public columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand'];
  public expandedElement: any | null;
  constructor(
    private readonly nomenclaturesService: NomenclatureService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.getNomenclaturesData();
  }

  getNomenclaturesData() {
    this.nomenclaturesService
      .getAllNomenclatures()
      .subscribe(
        (nomenclaturesResponse) =>
          (this.matTableDataSource = nomenclaturesResponse.results)
      );
  }

  addNomenclatureCategory() {
    const dialogRef = this.dialog.open(AddNomenclatureComponent, {
      height: '300px',
      width: '400px',
    });
    dialogRef.afterClosed().subscribe((data) => {
      if (data) {
        this.nomenclaturesService
          .addCategory(data.nomenclature)
          .subscribe((res) => {
            this.matTableDataSource.push(res);
            this.table.renderRows();
          });
      }
    });
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
            this.matTableDataSource[index] = res;
            this.table.renderRows();
          });
      }
    });
  }
}
