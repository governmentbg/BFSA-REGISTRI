import { Component, Inject, ViewChild } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableDataSource } from '@angular/material/table';
import { tap } from 'rxjs';
import { InspectionAttachmentsInterface } from 'src/app/home/inspections/interfaces/inspection-attachments-interface';
import { InspectionService } from 'src/app/home/inspections/services/inspection.service';

@Component({
  selector: 'app-view-attachments-dialog',
  templateUrl: './view-attachments-dialog.component.html',
  styleUrls: ['./view-attachments-dialog.component.scss'],
})
export class ViewAttachmentsDialogComponent {
  public displayedColumns: string[] = ['fileName', 'action'];
  @ViewChild(MatPaginator) paginator: MatPaginator;
  public dataSource: MatTableDataSource<any> = new MatTableDataSource();

  constructor(
    public dialogRef: MatDialogRef<ViewAttachmentsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Blob[],
    private readonly inspectionService: InspectionService,
    private _snackBar: MatSnackBar
  ) {}

  confirm() {
    //open next modal here
    this.dialogRef.close(true);
  }

  discard() {
    this.dialogRef.close(false);
  }

  ngOnInit() {
	console.log(this.data)
    this.dataSource.data = this.data;
  }

  downloadFile(file: InspectionAttachmentsInterface) {
    this.inspectionService
      .getFile(file.id)
      .pipe(
        tap({
          next: (res) => {
            console.log(res);
            const blob = new Blob(
              [Uint8Array.from(atob(res.resource), (c) => c.charCodeAt(0))],
              { type: res.contentType }
            );
            console.log(blob);
            window.saveAs(blob, file.fileName);
            this._snackBar.open('File downloaded successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this._snackBar.open(
              `Error while downloading the file: ${err.message}`,
              'Close'
            );
          },
        })
      )
      .subscribe();
  }
}
