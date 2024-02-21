import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import * as dayjs from 'dayjs';
import { DateAdapter } from '@angular/material/core';
import { NomenclatureService } from 'src/app/home/nomenclature/services/nomenclature.service';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';
import { InspectionType } from '../interfaces/inspection-type';
import { NomenclatureType } from '../interfaces/nomenclature-type';
import { UserInterface } from 'src/app/home/user/interfaces/user-interface';
import { UserService } from 'src/app/home/user/services/user.service';
import { RiskLevel } from '../interfaces/risk-level';
import { ActivatedRoute, Router } from '@angular/router';
import { tap } from 'rxjs';
import { InspectionService } from '../services/inspection.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-inspection-new',
  templateUrl: './inspection-new.component.html',
  styleUrls: ['./inspection-new.component.scss'],
})
export class InspectionNewComponent implements OnInit {
  public inspectionForm = this.fb.group({
    inspectionType: [null, Validators.required],
    users: [null, Validators.required],
    riskLevel: [null],
    reasonsCodes: [null],
    endDate: [dayjs(new Date()).format('YYYY-MM-DD'), Validators.required],
    description: [null, Validators.required],
    contractorId: [null],
    applicantFullName: [null],
    applicantIdentifier: [null],
    applicantEmail: [null],
  });

  // convenience getter for easy access to form fields
  get f(): { [key: string]: AbstractControl } {
    return this.inspectionForm.controls;
  }

  plannedInspectionReason =
    this.inspectionForm.controls['plannedInspectionReason'];
  reasonsCodesControl = this.inspectionForm.controls['reasonsCodes'];
  riskLevel = this.inspectionForm.controls['riskLevel'];

  inspectionTypes = Object.entries(InspectionType).filter(
    (el) => el[0] !== 'FOR_APPROVAL'
  );
  riskLevels = RiskLevel;
  users: UserInterface[];
  plannedInspectionReasons: NomenclatureInterface[];
  extraordinaryInspectionReasons: NomenclatureInterface[];
  minDate = new Date();
  filterDate = (d: Date | null): boolean => {
    const day = (d || new Date()).getDay();
    // Prevent Saturday and Sunday from being selected.
    return day !== 0 && day !== 6;
  };

  value: string;
  key: string;

  constructor(
    private fb: UntypedFormBuilder,
    private route: ActivatedRoute,
    private readonly userService: UserService,
    private readonly inspectionService: InspectionService,
    private readonly nomenclatureService: NomenclatureService,
    private _snackBar: MatSnackBar,
    private router: Router,
    private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('bg-BG');
  }

  ngOnInit() {
    console.log(this.inspectionTypes);
    console.log(this.route.snapshot.queryParams);
    for (const [key, value] of Object.entries(
      this.route.snapshot.queryParams
    )) {
      if (key === 'contractorId') {
        this.inspectionForm.get('contractorId')?.setValue(value);
      } else if (key === 'fullName') {
        this.inspectionForm.get('applicantFullName')?.setValue(value);
      } else if (key === 'identifier') {
        this.inspectionForm.get('applicantIdentifier')?.setValue(value);
      } else if (key === 'email') {
        this.inspectionForm.get('applicantEmail')?.setValue(value);
      } else {
        (this.key = key), (this.value = value);
      }
      console.log(`${key}: ${value}`);
    }

    //this.itemId = this.route.snapshot.queryParams;
    //console.log(this.itemId)
    // this.itemId = Object.keys(this.route.snapshot.queryParams).toString();
    // this.id = Object.values(this.route.snapshot.queryParams).toString();

    this.inspectionForm.addControl(
      this.key,
      this.fb.control(this.value, Validators.required)
    );

    this.getAllExperts();
    this.getPlannedInspectionReasons();
    this.getExtraordinaryInspectionReasons();

    this.f['inspectionType'].valueChanges.subscribe((value) => {
      if (value === InspectionType.PLANNED) {
        this.f['reasonsCodes'].enable();
        this.f['reasonsCodes'].reset();
        this.f['riskLevel'].enable();
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
        this.f['reasonsCodes'].reset();
        this.f['riskLevel'].disable();
      }
    });
  }

  ngAfterViewInit() {
    console.log(this.inspectionForm.value);
  }

  getPlannedInspectionReasons() {
    this.nomenclatureService
      .getSubNomenclatures(NomenclatureType.PLANNED_INSPECTION_REASON)
      .subscribe((nomenclatures: NomenclatureInterface[]) => {
        this.plannedInspectionReasons = nomenclatures;
      });
  }

  getExtraordinaryInspectionReasons() {
    this.nomenclatureService
      .getSubNomenclatures(NomenclatureType.EXTRAORDINARY_INSPECTION_REASON)
      .subscribe((nomenclatures: NomenclatureInterface[]) => {
        this.extraordinaryInspectionReasons = nomenclatures;
      });
  }

  getAllExperts() {
    this.userService.getUsers().subscribe((users: UserInterface[]) => {
      this.users = users;
    });
  }

  onSubmit() {
    this.f['endDate'].setValue(
      dayjs(this.f['endDate'].value).format('YYYY-MM-DD')
    );
    console.log(JSON.stringify(this.inspectionForm.value, null, 2));

    switch (this.key) {
      case 'facilityId':
        console.log(this.inspectionForm.value);
        this.inspectionService
          .createFacilityInspection(this.value, this.inspectionForm.value)
          .pipe(
            tap({
              next: () => {
                this._snackBar.open('Inspection created successfully', '', {
                  duration: 1000,
                });
                this.router.navigate(['/app/inspections']);
              },
              error: (err) => {
                this._snackBar.open(
                  `Error while creating the inspection: ${err.message}`,
                  'Close'
                );
              },
            })
          )
          .subscribe();
        break;
      case 'vehicleId':
        console.log(this.inspectionForm.value);
        this.inspectionService
          .createVehicleInspection(this.value, this.inspectionForm.value)
          .pipe(
            tap({
              next: () => {
                this._snackBar.open('Inspection created successfully', '', {
                  duration: 1000,
                });
                this.router.navigate(['/app/inspections']);
              },
              error: (err) => {
                this._snackBar.open(
                  `Error while creating the inspection: ${err.message}`,
                  'Close'
                );
              },
            })
          )
          .subscribe();
        break;
      default:
        console.log(`Sorry, we are out of ${this.value}.`);
    }
  }
}
