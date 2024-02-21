import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SettlementInterface } from '../../interfaces/settlement-interface';
import { SettlementService } from '../../services/settlement.service';
import { SettlementDialogComponent } from '../settlement-dialog/settlement-dialog.component';

@Component({
  selector: 'app-settlement-list',
  templateUrl: './settlement-list.component.html',
  styleUrls: ['./settlement-list.component.scss'],
})
export class SettlementListComponent implements OnInit {
  settlements: SettlementInterface[];

  constructor(
    private settlementService: SettlementService,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  initialize(): void {
    this.settlementService
      .getParentSettlements()
      .subscribe((settlements: SettlementInterface[]) => {
        this.settlements = settlements;
      });
  }

  addSettlement() {
    const dialogRef = this.dialog.open(SettlementDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef.afterClosed().subscribe((settlement: SettlementInterface) => {
      if (settlement) {
        console.log(settlement);
        this.settlementService.addSettlement(settlement).subscribe({
          next: (res) => {
            this.initialize();
            this._snackBar.open('Settlement added successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while adding the settlement: ${err.message}`,
              'Close'
            );
          },
        });
      }
    });
  }
}
