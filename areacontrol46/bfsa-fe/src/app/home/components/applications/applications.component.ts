import { AfterViewInit, Component, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { RecordService } from 'src/app/services/record.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CreateApplicationS2701Component } from './create-application-s2701/create-application-s2701.component';
import { EditApplicationS2701Component } from './edit-application-s2701/edit-application-s2701.component';
import { EditApplicationS3180Component } from './edit-application-s3180/edit-application-s3180.component';
import { ExpertModalComponent } from './expert-modal/expert-modal.component';
import { FinanceModalComponent } from './finance-modal/finance-modal.component';
import { recordStatus } from 'src/app/enums/recordStatus';
import { MatTableDataSource } from '@angular/material/table';
import { TokenStorageService } from 'src/app/services/token.service';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { TranslateService } from '@ngx-translate/core';
import { Title } from '@angular/platform-browser';
import { CreateApplicationS3180Component } from './create-application-s3180/create-application-s3180.component';
import { CreateApplicationS3181Component } from './create-application-s3181/create-application-s3181.component';
import { EditApplicationS3181Component } from './edit-application-s3181/edit-application-s3181.component';
import { CreateApplicationS3182Component } from './create-application-s3182/create-application-s3182.component';
import { EditApplicationS3182Component } from './edit-application-s3182/edit-application-s3182.component';
import { MatPaginator } from '@angular/material/paginator';
import { CreateApplicationS1590Component } from './create-application-s1590/create-application-s1590.component';
import { EditApplicationS1590Component } from './edit-application-s1590/edit-application-s1590.component';
import { CreateApplicationS2699Component } from './create-application-s2699/create-application-s2699.component';
import { EditApplicationS2699Component } from './edit-application-s2699/edit-application-s2699.component';
import { CreateApplicationS2272Component } from './create-application-s2272/create-application-s2272.component';
import { EditApplicationS2272Component } from './edit-application-s2272/edit-application-s2272.component';
import { CreateApplicationS2702Component } from './create-application-s2702/create-application-s2702.component';
import { EditApplicationS2702Component } from './edit-application-s2702/edit-application-s2702.component';
import { EditApplicationS1199Component } from './edit-application-s1199/edit-application-s1199.component';
import { EditApplicationS1811Component } from './edit-application-s1811/edit-application-s1811.component';
import { EditApplicationS503Component } from './edit-application-s503/edit-application-s503.component';
import { EditApplicationS2869Component } from './edit-application-s2869/edit-application-s2869.component';
import { EditApplicationS3125Component } from './edit-application-s3125/edit-application-s3125.component';
import { EditApplicationS2700Component } from './edit-application-s2700/edit-application-s2700.component';
import { ViewAttachmentsDialogComponent } from './view-attachments-dialog/view-attachments-dialog.component';
import { EditApplicationS502Component } from './edit-application-s502/edit-application-s502.component';
import { EditApplicationS2698Component } from './edit-application-s2698/edit-application-s2698.component';
import { ApplicationService } from 'src/app/services/application.service';
import { EditApplicationS2711Component } from './edit-application-s2711/edit-application-s2711.component';
import { EditApplicationS7694Component } from './edit-application-s7694/edit-application-s7694.component';
import { EditApplicationS2697Component } from './edit-application-s2697/edit-application-s2697.component';
import { EditApplicationS7693Component } from './edit-application-s7693/edit-application-s7693.component';
import { EditApplicationS7695Component } from './edit-application-s7695/edit-application-s7695.component';
import { EditApplicationS2695Component } from './edit-application-s2695/edit-application-s2695.component';
import { EditApplicationS3201Component } from './edit-application-s3201/edit-application-s3201.component';
import { EditApplicationS2170Component } from './edit-application-s2170/edit-application-s2170.component';
import { EditApplicationS3362Component } from './edit-application-s3362/edit-application-s3362.component';
import { EditApplicationS3363Component } from './edit-application-s3363/edit-application-s3363.component';
import * as dayjs from 'dayjs';
import { EditApplicationS3365Component } from './edit-application-s3365/edit-application-s3365.component';
import { EditApplicationS13661Component } from './edit-application-s13661/edit-application-s13661.component';
import { EditApplicationS13662Component } from './edit-application-s13662/edit-application-s13662.component';
import { EditApplicationS7692Component } from './edit-application-s7692/edit-application-s7692.component';
import { EditApplicationS7691Component } from './edit-application-s7691/edit-application-s7691.component';
import { EditApplicationS7696Component } from './edit-application-s7696/edit-application-s7696.component';
import { EditApplicationS2274Component } from './edit-application-s2274/edit-application-s2274.component';

@Component({
  selector: 'app-applications',
  templateUrl: './applications.component.html',
  styleUrls: ['./applications.component.scss'],
})
export class ApplicationsComponent implements AfterViewInit {
  public pageSize: number = 20;
  public pageIndex: number = 0;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  public dataSource: MatTableDataSource<any> = new MatTableDataSource();

  public recordStatuses = Object.entries(recordStatus);
  public searchForm: FormGroup;
  public userToken = this.tokenStorage.getUser();
  public dialogOpened: boolean = false;
  public displayedColumns: string[] = [
    'entryDate',
    'entryNumber',
    'requestorIdentifier',
    'requestorFullName',
    'applicantIdentifier',
    'applicantFullName',
    'approvalDocumentStatus',
    'recordPrice',
    'applicationPrice',
    'serviceType',
    'action',
  ];

  constructor(
    private title: Title,
    private translate: TranslateService,
    private _dialog: MatDialog,
    private fb: UntypedFormBuilder,
    private readonly recordService: RecordService,
    private _snackBar: MatSnackBar,
    private readonly tokenStorage: TokenStorageService,
    private readonly applicationService: ApplicationService
  ) {}

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY HH:mm') + 'ч';
  }

  ngOnInit() {
    this.getRecordsByBranchId();
    this.searchForm = this.fb.group({
      q: [''],
      recordStatus: [''],
    });
    this.translate.get('nav.applications').subscribe((title) => {
      this.title.setTitle(title);
    });
  }

  // createApplicationS3180Dialog(): void {
  //   let dialogRef = this._dialog.open(CreateApplicationS3180Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS2701Dialog(): void {
  //   let dialogRef = this._dialog.open(CreateApplicationS2701Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }
  // createApplicationS2272Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS2272Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS2702Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS2702Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS3181Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS3181Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS3182Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS3182Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS1590Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS1590Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  // createApplicationS2699Dialog() {
  //   let dialogRef = this._dialog.open(CreateApplicationS2699Component, {
  //     width: '90vw',
  //     height: '90vh',
  //     autoFocus: false,
  //   });

  //   dialogRef.afterClosed().subscribe((result) => {
  //     if (result) {
  //       this._snackBar.open('Record created successfully', '', {
  //         duration: 1000,
  //       });
  //       this.getRecordsByBranchId();
  //     }
  //   });
  // }

  editApplicationDialog(el: any): void {
    const id = el.recordId;
    const serviceType = el.serviceType.toLowerCase();
    if (!this.dialogOpened) {
      this.recordService
        .getRecordbyIdAndServiceType(serviceType, id)
        .subscribe((record) => {
          console.log(record);
          record.id = id;
          let dialogRef;
          switch (serviceType) {
            case 's503':
              dialogRef = this._dialog.open(EditApplicationS503Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's1199':
              dialogRef = this._dialog.open(EditApplicationS1199Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's1590':
              dialogRef = this._dialog.open(EditApplicationS1590Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's1811':
              dialogRef = this._dialog.open(EditApplicationS1811Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2272':
              dialogRef = this._dialog.open(EditApplicationS2272Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2699':
              dialogRef = this._dialog.open(EditApplicationS2699Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2701':
              dialogRef = this._dialog.open(EditApplicationS2701Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2702':
              dialogRef = this._dialog.open(EditApplicationS2702Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2869':
              dialogRef = this._dialog.open(EditApplicationS2869Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3180':
              dialogRef = this._dialog.open(EditApplicationS3180Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3181':
              dialogRef = this._dialog.open(EditApplicationS3181Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3182':
              dialogRef = this._dialog.open(EditApplicationS3182Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3125':
              dialogRef = this._dialog.open(EditApplicationS3125Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2700':
              dialogRef = this._dialog.open(EditApplicationS2700Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's502':
              dialogRef = this._dialog.open(EditApplicationS502Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2698':
              dialogRef = this._dialog.open(EditApplicationS2698Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2711':
              dialogRef = this._dialog.open(EditApplicationS2711Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7691':
              dialogRef = this._dialog.open(EditApplicationS7691Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7692':
              dialogRef = this._dialog.open(EditApplicationS7692Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7693':
              dialogRef = this._dialog.open(EditApplicationS7693Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7694':
              dialogRef = this._dialog.open(EditApplicationS7694Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7695':
              dialogRef = this._dialog.open(EditApplicationS7695Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's7696':
              dialogRef = this._dialog.open(EditApplicationS7696Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2697':
              dialogRef = this._dialog.open(EditApplicationS2697Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2695':
              dialogRef = this._dialog.open(EditApplicationS2695Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3201':
              dialogRef = this._dialog.open(EditApplicationS3201Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2170':
              dialogRef = this._dialog.open(EditApplicationS2170Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3362':
              dialogRef = this._dialog.open(EditApplicationS3362Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3363':
              dialogRef = this._dialog.open(EditApplicationS3363Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's3365':
              dialogRef = this._dialog.open(EditApplicationS3365Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's2274':
              dialogRef = this._dialog.open(EditApplicationS2274Component, {
                maxWidth: '100vw',
                maxHeight: '100vh',
                height: '95%',
                width: '90%',
                autoFocus: false,
                data: record,
              });
              dialogRef.afterClosed().subscribe((result) => {
                this.dialogOpened = false;
                if (result) {
                  this._snackBar.open('Record edited successfully', '', {
                    duration: 1000,
                  });
                  this.getRecordsByBranchId();
                }
              });
              break;
            case 's1366':
              if (!record.applicantTypeCode) {
                dialogRef = this._dialog.open(EditApplicationS13661Component, {
                  maxWidth: '100vw',
                  maxHeight: '100vh',
                  height: '95%',
                  width: '90%',
                  autoFocus: false,
                  data: record,
                });
                dialogRef.afterClosed().subscribe((result) => {
                  this.dialogOpened = false;
                  if (result) {
                    this._snackBar.open('Record edited successfully', '', {
                      duration: 1000,
                    });
                    this.getRecordsByBranchId();
                  }
                });
              } else {
                dialogRef = this._dialog.open(EditApplicationS13662Component, {
                  maxWidth: '100vw',
                  maxHeight: '100vh',
                  height: '95%',
                  width: '90%',
                  autoFocus: false,
                  data: record,
                });
                dialogRef.afterClosed().subscribe((result) => {
                  this.dialogOpened = false;
                  if (result) {
                    this._snackBar.open('Record edited successfully', '', {
                      duration: 1000,
                    });
                    this.getRecordsByBranchId();
                  }
                });
              }
              break;
          }
        });
    }
  }

  public get isExpert() {
    return this.userToken?.roles?.includes('ROLE_EXPERT');
  }

  public get isFinance() {
    return this.userToken?.roles?.includes('ROLE_FINANCE');
  }

  public get isAdmin() {
    return this.userToken?.roles?.includes('ROLE_ADMIN');
  }

  openExpertDialog(el: any) {
    const id = el.recordId;
    const recordPrice = el.recordPrice;
    let dialogRef;
    if (recordPrice == -1) {
      dialogRef = this._dialog.open(ExpertModalComponent, {
        width: '40vw',
        autoFocus: false,
        // data: el,
      });
    } else {
      dialogRef = this._dialog.open(ExpertModalComponent, {
        width: '40vw',
        autoFocus: false,
        data: recordPrice,
        // data: el,
      });
    }

    dialogRef.afterClosed().subscribe((recordPrice) => {
      if (recordPrice) {
        this.recordService.sendForPayment(id, recordPrice).subscribe((res) => {
          if (res) {
            this.getRecordsByBranchId();
          }
        });
      }
    });
  }

  openFinanceDialog(el: any) {
    const id = el.recordId;
    let price = el.recordPrice;
    const serviceType = el.serviceType.toLowerCase();
    if (this.isPaymentInConfirmationStatusApplication(el)) {
      price = el.applicationPrice;
      // we override price if there is a second payment
      let dialogRef = this._dialog.open(FinanceModalComponent, {
        width: '40vw',
        autoFocus: false,
        data: price,
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.applicationService
            .confirmApplicationPayment(serviceType, id)
            .subscribe((res) => {
              if (res) {
                this.getRecordsByBranchId();
              }
            });
        }
      });
    } else {
      let dialogRef = this._dialog.open(FinanceModalComponent, {
        width: '40vw',
        autoFocus: false,
        data: price,
      });
      dialogRef.afterClosed().subscribe((result) => {
        if (result) {
          this.recordService.confirmRecordPayment(id).subscribe((res) => {
            if (res) {
              this.getRecordsByBranchId();
            }
          });
        }
      });
    }
  }

  openFilesDialog(el: any) {
    const id = el.recordId;
    this.recordService.getRecordAttachments(id).subscribe((data) => {
      console.log('files:', data);
      this._dialog
        .open(ViewAttachmentsDialogComponent, {
          width: '70vw',
          height: '80vh',
          data,
        })
        .afterClosed()
        .subscribe((res) => console.log(res));
    });
  }

  isServicePaid(el: any) {
    return el.recordPrice !== 0 && el.recordStatus === 'ENTERED';
  }

  isPaymentInConfirmationStatusRecord(el: any) {
    return el.recordStatus === 'PAYMENT_CONFIRMATION';
  }

  isPaymentInConfirmationStatusApplication(el: any) {
    return el.applicatonStatus === 'PAYMENT_CONFIRMATION';
  }

  isStatusPaid(el: any) {
    return (
      el.recordStatus === 'PAYMENT_CONFIRMED' ||
      el.recordStatus === 'PROCESSING'
    );
  }

  isStatusRejectedOrApproved(el: any) {
    return (
      el.recordStatus === 'FINAL_REJECTED' ||
      el.recordStatus === 'FINAL_APPROVED'
    );
  }

  isStatusInspectionCompleted(el: any) {
    return el.recordStatus === 'INSPECTION_COMPLETED';
  }

  translateStatus(status: string) {
    const lang = localStorage.getItem('lang');
    const statuses = Object.entries(recordStatus);
    const index = statuses.findIndex((el) => el[0] === status);
    if (lang === 'bg') {
      return statuses[index][1];
    }
    return status;
  }

  isApplicationRecordPriceZero(el: any) {
    if (el.recordPrice === 0) {
      return true;
    }
    return false;
  }

  isApplicationStatusEntered(el: any) {
    if (el.recordStatus === 'ENTERED') {
      return true;
    }
    return false;
  }

  getRecordsByBranchId() {
    const branchId = this.userToken?.branchId;
    if (this.paginator?.pageIndex) {
      this.recordService
        .getRecordsByBranchId(
          branchId,
          this.paginator?.pageIndex,
          this.paginator?.pageSize
        )
        .subscribe((res) => {
          this.dataSource.data = res.content;
          this.paginator.length = res.totalElements;
        });
    } else {
      this.recordService
        .getRecordsByBranchId(branchId, this.pageIndex, this.pageSize)
        .subscribe((res) => {
          console.log(res);
          this.dataSource.data = res.content;
          this.paginator.length = res.totalElements;
        });
    }
  }

  ngAfterViewInit() {
    this.paginator?.page.subscribe(() => this.getRecordsByBranchId());
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  submitForm() {
    this.recordService
      .search(this.searchForm.value)
      .subscribe((res) => (this.dataSource.data = res.content));
  }
}
