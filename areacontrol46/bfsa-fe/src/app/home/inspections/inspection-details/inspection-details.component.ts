import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { DateAdapter } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Params, Router } from '@angular/router';
import * as dayjs from 'dayjs';
import { EMPTY, filter, mergeMap, switchMap, tap } from 'rxjs';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { NomenclatureService } from 'src/app/home/nomenclature/services/nomenclature.service';
import { UserInterface } from 'src/app/home/user/interfaces/user-interface';
import { UserService } from 'src/app/home/user/services/user.service';
import { InspectionAttachmentsInterface } from '../interfaces/inspection-attachments-interface';
import { InspectionInterface } from '../interfaces/inspection-interface';
import { InspectionType } from '../interfaces/inspection-type';
import { NomenclatureType } from '../interfaces/nomenclature-type';
import { RiskLevel } from '../interfaces/risk-level';
import { InspectionService } from '../services/inspection.service';
import { InspectionConfirmDialogComponent } from '../inspection-confirm-dialog/inspection-confirm-dialog.component';

@Component({
  selector: 'app-inspection-details',
  templateUrl: './inspection-details.component.html',
  styleUrls: ['./inspection-details.component.scss'],
})
export class InspectionDetailsComponent implements OnInit {
  public inspectionForm = this.fb.group({
    id: [null],
    inspectionType: [null, Validators.required],
    users: [null, Validators.required],
    riskLevel: [null],
    reasonsCodes: [null],
    recordId: [null],
    vehicleId: [null],
    facilityId: [null],
    endDate: [null, Validators.required],
    description: [null, Validators.required],
  });

  public inspectionResultDocumentTypeControl = this.fb.control(null);
  public endDate = this.inspectionForm.controls['endDate'];

  public inspectionDTO: {
    id: string;
    applicantFullName: string;
    applicantIdentifier: string;
  };

  public inspectionTypes = InspectionType;
  public riskLevels = RiskLevel;
  public users: UserInterface[];
  public plannedInspectionReasons: NomenclatureInterface[];
  public extraordinaryInspectionReasons: NomenclatureInterface[];
  public attachments: InspectionAttachmentsInterface[];
  public inspectionResultDocumentTypes: NomenclatureInterface[];
  public inspection: InspectionInterface;
  public minDate = new Date();

  filterDate = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    // Prevent Saturday and Sunday from being selected.
    return day !== 0 && day !== 6;
  };

  constructor(
    private fb: UntypedFormBuilder,
    private readonly inspectionService: InspectionService,
    private readonly nomenclatureService: NomenclatureService,
    public dialog: MatDialog,
    public activatedRoute: ActivatedRoute,
    private router: Router,
    private _snackBar: MatSnackBar,
    private dateAdapter: DateAdapter<Date>,
    private readonly userService: UserService
  ) {
    this.dateAdapter.setLocale('bg-BG');
  }

  // convenience getter for easy access to form fields
  get f(): { [key: string]: AbstractControl } {
    return this.inspectionForm.controls;
  }

  ngOnInit() {
    this.initializeInspection();

    this.f['inspectionType'].valueChanges.subscribe((value) => {
      if (value === InspectionType.PLANNED) {
        this.f['reasonsCodes'].enable();
        this.f['reasonsCodes'].addValidators(Validators.required);
        this.f['reasonsCodes'].reset();
        this.f['riskLevel'].enable();
        this.f['riskLevel'].addValidators(Validators.required);
      }
      if (
        value === InspectionType.OFFICIAL ||
        value === InspectionType.FOR_REGISTRATION
      ) {
        this.f['reasonsCodes'].disable();
        this.f['riskLevel'].disable();
      }
      if (value === InspectionType.EXTRAORDINARY) {
        this.f['reasonsCodes'].enable();
        this.f['reasonsCodes'].addValidators(Validators.required);
        this.f['reasonsCodes'].reset();
        this.f['riskLevel'].disable();
      }
    });
  }

  initializeInspection() {
    this.activatedRoute.params
      .pipe(
        switchMap((params: any) => {
          this.inspectionDTO = params;
          return this.inspectionService.getInspection(this.inspectionDTO.id);
        }),
        tap((inspection: InspectionInterface) => {
          this.inspection = inspection;
          this.inspectionForm.patchValue({
            id: inspection.id,
            description: inspection.description ?? 'n/a',
            endDate: inspection.endDate,
            inspectionType: inspection.inspectionType,
            riskLevel: inspection.riskLevel,
            reasonsCodes: inspection.reasonsCodes,
            users: inspection.users,
            facilityId: inspection.facilityId,
            vehicleId: inspection.vehicleId,
            recordId: inspection.recordId,
          });

          this.getInspectionResultDocumentTypes();
          this.getAllExperts();
          this.getPlannedInspectionReasons();
          this.getExtraordinaryInspectionReasons();
          this.initializeAttachments();
        })
      )
      .subscribe();
  }

  initializeAttachments() {
    this.activatedRoute.params
      .pipe(
        switchMap((params: Params) => {
          const inspectionId = params['id'];
          return this.inspectionService.getAttachments(inspectionId);
        }),
        tap((attachments: InspectionAttachmentsInterface[]) => {
          this.attachments = attachments;
          this.inspectionResultDocumentTypeControl.setValue(
            attachments[0]?.docTypeCode
          );
        })
      )
      .subscribe();
  }

  onFileSelect(event: any): void {
    if (event.target.files.length > 0) {
      const file = event.target.files[0];
      const formData = new FormData();
      formData.append('file', file);
      this.inspectionService
        .attach(
          this.inspection.id,
          this.inspectionResultDocumentTypeControl.value,
          formData
        )
        .subscribe(() => {
          this.initializeAttachments();
        });
    }
  }

  delete(file: InspectionAttachmentsInterface) {
    const title = 'Искате ли да изтриете файла?';
    this.dialog
      .open(InspectionConfirmDialogComponent, {
        width: '400px',
        data: { title },
      })
      .afterClosed()
      .pipe(
        filter((result) => result),
        mergeMap(() => this.inspectionService.deleteFile(file.id)),
        tap({
          next: () => {
            this.inspectionResultDocumentTypeControl.reset();
            this.initializeAttachments();
            this._snackBar.open('File deleted successfully', '', {
              duration: 1000,
            });
          },
          error: (err) => {
            this.initializeAttachments();
            this._snackBar.open(
              `Error while deleting the file: ${err.message}`,
              'Close'
            );
          },
        })
      )
      .subscribe();
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

  complete() {
    const title = 'Искате ли да завършите проверката?';
    this.dialog
      .open(InspectionConfirmDialogComponent, {
        data: { title },
      })
      .afterClosed()
      .pipe(
        filter((result) => result),
        mergeMap(() => {
          this.f['endDate'].setValue(
            dayjs(this.f['endDate'].value).format('YYYY-MM-DD')
          );
          if (this.inspection.recordId) {
            console.log('recordId inspection completed');
            this.f['facilityId'].disable();
            this.f['vehicleId'].disable();
            return this.inspectionService.completeInspection(
              this.f['recordId'].value,
              this.inspectionForm.value
            );
          }
          if (this.inspection.facilityId) {
            console.log('facilityId inspection completed');
            this.f['recordId'].disable();
            this.f['vehicleId'].disable();
            return this.inspectionService.completeFacilityInspection(
              this.inspection.facilityId,
              this.inspectionForm.value
            );
          }
          if (this.inspection.vehicleId) {
            console.log('vehicleId inspection completed');
            this.f['recordId'].disable();
            this.f['facilityId'].disable();
            return this.inspectionService.completeVehicleInspection(
              this.inspection.vehicleId,
              this.inspectionForm.value
            );
          }
          return EMPTY;
        }),
        tap({
          next: () => {
            this._snackBar.open('Inspection completed successfully', '', {
              duration: 1000,
            });
            this.router.navigate(['/app/inspections']);
          },
          error: (err) => {
            this._snackBar.open(
              `Error while compliting the inspection: ${err.message}`,
              'Close'
            );
          },
        })
      )
      .subscribe();
  }

  getInspectionResultDocumentTypes() {
    this.nomenclatureService
      .getSubNomenclatures(NomenclatureType.INSPECTION_RESULT_DOCUMENTTYPE)
      .pipe(
        tap((nomenclatures: NomenclatureInterface[]) => {
          console.log('DocumentTypes', nomenclatures);
          this.inspectionResultDocumentTypes = nomenclatures;
        })
      )
      .subscribe();
  }

  getPlannedInspectionReasons() {
    this.nomenclatureService
      .getSubNomenclatures(NomenclatureType.PLANNED_INSPECTION_REASON)
      .pipe(
        tap((nomenclatures: NomenclatureInterface[]) => {
          this.plannedInspectionReasons = nomenclatures;
        })
      )
      .subscribe();
  }

  getExtraordinaryInspectionReasons() {
    this.nomenclatureService
      .getSubNomenclatures(NomenclatureType.EXTRAORDINARY_INSPECTION_REASON)
      .pipe(
        tap((nomenclatures: NomenclatureInterface[]) => {
          this.extraordinaryInspectionReasons = nomenclatures;
        })
      )
      .subscribe();
  }

  getAllExperts() {
    this.userService
      .getUsers()
      .pipe(
        tap((users: UserInterface[]) => {
          this.users = users;
        })
      )
      .subscribe();
  }

  onSubmit() {
    this.f['endDate'].setValue(
      dayjs(this.f['endDate'].value).format('YYYY-MM-DD')
    );
    console.log(this.inspectionForm.value);

    this.inspectionService
      .updateInspection(this.inspection.id, this.inspectionForm.value)
      .subscribe({
        next: () => {
          this._snackBar.open('Inspection updated successfully', '', {
            duration: 1000,
          });
          this.inspectionForm.markAsPristine();
        },
        error: (err) => {
          this._snackBar.open(
            `Error while updating the inspection: ${err.message}`,
            'Close'
          );
        },
      });
  }

  normalizeUsers(users: UserInterface[]) {
    if (users) {
      return users
        ?.filter((item) => this.inspection.users.includes(item.id))
        .map((item) => item.fullName)
        .join(', ');
    }
    return null;
  }

  normalizePlannedInspectionReasons(reasons: string[]) {
    if (this.plannedInspectionReasons) {
      return this.plannedInspectionReasons
        ?.filter((item) => reasons.includes(item.code))
        .map((item) => item.name)
        .join(', ');
    }
    return null;
  }

  normalizeExtraordinaryInspectionReasons(reasons: string[]) {
    if (this.extraordinaryInspectionReasons) {
      return this.extraordinaryInspectionReasons
        ?.filter((item) => reasons.includes(item.code))
        .map((item) => item.name)
        .join(', ');
    }
    return null;
  }

  formatDate(date: string) {
    return dayjs(date).format('DD.MM.YYYY');
  }

  getInspectionResultDocumentTypeNameByCode(code: string) {
    if (code) {
      return this.inspectionResultDocumentTypes?.find((t) => t.code === code)
        ?.name;
    }
    return '';
  }
}
