import { Component, OnInit } from '@angular/core';
import { FormArray, FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { ActivityGroupInterface } from '../../interfaces/activity-group-interface';
import { ActivityGroupService } from '../../services/activity-group.service';
import { ActivityGroupDialogComponent } from '../activity-group-dialog/activity-group-dialog.component';
import { Location } from '@angular/common';
import { NomenclatureService } from 'src/app/home/nomenclature/services/nomenclature.service';
import { NomenclatureInterface } from 'src/app/home/nomenclature/interfaces/nomenclature-interface';

export enum ActivityCategories {
  relatedActivityCategoriesCode = '00800',
  associatedActivityCategoriesCode = '00800',
  animalSpeciesCode = '00900',
  remarksCode = '01000',
}

@Component({
  selector: 'app-activity-group-item',
  templateUrl: './activity-group-item.component.html',
  styleUrls: ['./activity-group-item.component.scss'],
})
export class ActivityGroupItemComponent implements OnInit {
  activityGroup: ActivityGroupInterface;
  activityCategoriesForm = this.fb.group({
    relatedActivityCategories: this.fb.array([]),
    associatedActivityCategories: this.fb.array([]),
    animalSpecies: this.fb.array([]),
    remarks: this.fb.array([]),
  });

  relatedActivityCategories = this.activityCategoriesForm.controls[
    'relatedActivityCategories'
  ] as FormArray;

  associatedActivityCategories = this.activityCategoriesForm.controls[
    'associatedActivityCategories'
  ] as FormArray;

  animalSpecies = this.activityCategoriesForm.controls[
    'animalSpecies'
  ] as FormArray;

  remarks = this.activityCategoriesForm.controls['remarks'] as FormArray;

  constructor(
    private fb: FormBuilder,
    private activityGroupService: ActivityGroupService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private _snackBar: MatSnackBar,
    public dialog: MatDialog,
    private location: Location,
    private nomenclatureService: NomenclatureService
  ) {}

  ngOnInit(): void {
    this.initialize();
  }

  updateActivityCategories() {
    const relatedActivityCategories =
      this.activityCategoriesForm.value.relatedActivityCategories
        ?.filter((item: any) => item.checked === true)
        .map((item: any) => item.code);
    const associatedActivityCategories =
      this.activityCategoriesForm.value.associatedActivityCategories
        ?.filter((item: any) => item.checked === true)
        .map((item: any) => item.code);
    const animalSpecies =
      this.activityCategoriesForm.value.animalSpecies
        ?.filter((item: any) => item.checked === true)
        .map((item: any) => item.code);
    const remarks =
      this.activityCategoriesForm.value.remarks
        ?.filter((item: any) => item.checked === true)
        .map((item: any) => item.code);
    const activityCategories = [
      ...relatedActivityCategories!,
      ...associatedActivityCategories!,
      ...animalSpecies!,
      ...remarks!
    ];

    console.log(activityCategories);
    this.activityGroupService.updateActivityGroup(this.activityGroup.id, activityCategories)
            .subscribe({
              next: (res) => {
                this.initialize();
                this._snackBar.open('Activity Categories updated successfully', '', {
                  duration: 1000,
                });
              },
              error: (err) => {
                this._snackBar.open(
                  `Error while updating the Activity Categories group: ${err.message}`,
                  'Close'
                );
              },
            });
  }

  initialize() {
    this.activatedRoute.params.subscribe((params: Params) => {
      const id = params['id'];
      this.activityGroupService
        .getActivityGroup(id)
        .subscribe((activityGroup: ActivityGroupInterface) => {
          this.activityGroup = activityGroup;

          this.nomenclatureService
            .getSubNomenclatures(
              ActivityCategories.relatedActivityCategoriesCode
            )
            .subscribe((nomenclatures: NomenclatureInterface[]) => {
              nomenclatures.forEach((item) => {
                const control = this.fb.group({
                  code: item.code,
                  name: item.name,
                  checked: this.hasRelatedActivityCategories(item.code),
                });
                this.relatedActivityCategories.push(control);
              });
            });

          this.nomenclatureService
            .getSubNomenclatures(
              ActivityCategories.associatedActivityCategoriesCode
            )
            .subscribe((nomenclatures: NomenclatureInterface[]) => {
              nomenclatures.forEach((item) => {
                const control = this.fb.group({
                  code: item.code,
                  name: item.name,
                  checked: this.hasAssociatedActivityCategories(item.code),
                });
                this.associatedActivityCategories.push(control);
              });
            });

          this.nomenclatureService
            .getSubNomenclatures(ActivityCategories.animalSpeciesCode)
            .subscribe((nomenclatures: NomenclatureInterface[]) => {
              nomenclatures.forEach((item) => {
                const control = this.fb.group({
                  code: item.code,
                  name: item.name,
                  checked: this.hasAnimalSpecies(item.code),
                });
                this.animalSpecies.push(control);
              });
            });

          this.nomenclatureService
            .getSubNomenclatures(ActivityCategories.remarksCode)
            .subscribe((nomenclatures: NomenclatureInterface[]) => {
              nomenclatures.forEach((item) => {
                const control = this.fb.group({
                  code: item.code,
                  name: item.name,
                  checked: this.hasRemarks(item.code),
                });
                this.remarks.push(control);
              });
            });
        });
    });
  }

  hasRelatedActivityCategories(code: string) {
    return this.activityGroup.relatedActivityCategories
      .map((item) => item.code)
      .includes(code);
  }

  hasAssociatedActivityCategories(code: string) {
    return this.activityGroup.associatedActivityCategories
      .map((item) => item.code)
      .includes(code);
  }

  hasAnimalSpecies(code: string) {
    return this.activityGroup.animalSpecies
      .map((item) => item.code)
      .includes(code);
  }

  hasRemarks(code: string) {
    return this.activityGroup.remarks.map((item) => item.code).includes(code);
  }

  back(): void {
    this.location.back();
    this.activityCategoriesForm.controls['relatedActivityCategories'].clear();
    this.activityCategoriesForm.controls[
      'associatedActivityCategories'
    ].clear();
    this.activityCategoriesForm.controls['animalSpecies'].clear();
    this.activityCategoriesForm.controls['remarks'].clear();
  }

  navigateTo(activityGroup: ActivityGroupInterface): void {
    this.router.navigate(['/app/activity-group', activityGroup.id]);
  }

  addSubActivityGroup() {
    const dialogRef = this.dialog.open(ActivityGroupDialogComponent, {
      width: '400px',
      data: { isAdd: true },
    });
    dialogRef
      .afterClosed()
      .subscribe((activityGroup: ActivityGroupInterface) => {
        if (activityGroup) {
          console.log(activityGroup);
          this.activityGroupService
            .addSubActivityGroup(this.activityGroup.id, activityGroup)
            .subscribe({
              next: (res) => {
                this.initialize();
                this._snackBar.open('Activity Group added successfully', '', {
                  duration: 1000,
                });
              },
              error: (err) => {
                this._snackBar.open(
                  `Error while adding the Activity Group: ${err.message}`,
                  'Close'
                );
              },
            });
        }
      });
  }

  editActivityGroup(activityGroup: ActivityGroupInterface) {
    const dialogRef = this.dialog.open(ActivityGroupDialogComponent, {
      width: '400px',
      data: { isAdd: false, activityGroup },
    });
    dialogRef
      .afterClosed()
      .subscribe((activityGroup: ActivityGroupInterface) => {
        if (activityGroup) {
          console.log(activityGroup);
          this.activityGroupService
            .updateActivityGroup(activityGroup.id, activityGroup)
            .subscribe({
              next: (res) => {
                this.initialize();
                this._snackBar.open('Activity Group updated successfully', '', {
                  duration: 1000,
                });
              },
              error: (err) => {
                this._snackBar.open(
                  `Error while updating the Activity Group: ${err.message}`,
                  'Close'
                );
              },
            });
        }
      });
  }

  saveRelatedActivityCategories(options: any): void {
    const relatedActivityCategories = options.map((item: any) => {
      return { item: item.value, checked: true };
    });
    this.activityGroup.relatedActivityCategories = [
      ...relatedActivityCategories,
    ];
    console.log(this.activityGroup);
  }
}
