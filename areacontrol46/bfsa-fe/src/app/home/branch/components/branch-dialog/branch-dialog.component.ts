import { Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  finalize,
  fromEvent,
  of,
  switchMap,
  tap,
} from 'rxjs';
import { SettlementService } from 'src/app/home/settlement/services/settlement.service';
import { BranchInterface } from '../../interfaces/branch-interface';

@Component({
  selector: 'app-branch-dialog',
  templateUrl: './branch-dialog.component.html',
  styleUrls: ['./branch-dialog.component.scss'],
})
export class BranchDialogComponent implements OnInit {
  dialogForm = this.fb.group({
    id: [''],
    identifier: [''],
    address: [''],
    email: [''],
    phone1: [''],
    phone2: [''],
    phone3: [''],
    description: [''],
    name: [''],
    main: false,
    enabled: true,
    settlementCode: [null, [Validators.required]],
  });
  filteredSettlements: any;
  selectedSettlementCode: string | null = null;
  isLoading = false;
  minLengthTerm = 3;
  isReadOnly: boolean = false;

  constructor(
    private fb: UntypedFormBuilder,
    private settlementService: SettlementService,
    public dialogRef: MatDialogRef<BranchDialogComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: {
      isAdd: boolean;
      branch: BranchInterface;
    }
  ) {
    dialogRef.disableClose = true;
    fromEvent(document, 'keyup').subscribe((event: any) => {
      if (event.key === 'Escape') {
        this.dialogRef.close();
      }
    });
  }

  ngOnInit(): void {
    this.selectedSettlementCode = this.data.branch?.settlementCode ?? null;
    this.search();

    if (!this.data.isAdd) {
      this.settlementService
        .getInfo(this.selectedSettlementCode)
        .subscribe((settlementInfo: string) => {
          this.dialogForm.controls['settlementCode'].setValue(settlementInfo, {
            emitEvent: false,
          });
          this.isReadOnly = true;
        });

      this.dialogForm.patchValue({
        id: this.data.branch?.id,
        identifier: this.data.branch?.identifier,
        address: this.data.branch?.address,
        email: this.data.branch?.email,
        phone1: this.data.branch?.phone1,
        phone2: this.data.branch?.phone2,
        phone3: this.data.branch?.phone3,
        description: this.data.branch?.description,
        name: this.data.branch?.name,
        main: this.data.branch?.main,
        enabled: this.data.branch?.enabled,
      });
    }
    if (this.data.isAdd) {
      this.dialogForm.removeControl('id');
    }
  }

  onSelected(code: string) {
    this.selectedSettlementCode = code;
    this.settlementService
      .getInfo(this.selectedSettlementCode)
      .subscribe((settlementInfo: string) => {
        this.dialogForm.controls['settlementCode'].setValue(settlementInfo);
        this.isReadOnly = true;
      });
  }

  clearSelection() {
    this.selectedSettlementCode = null;
    this.dialogForm.controls['settlementCode'].setValue('');
    this.filteredSettlements = [];
    this.isReadOnly = false;
  }

  search() {
    this.dialogForm.controls['settlementCode'].valueChanges
      .pipe(
        filter((res) => {
          if (this.selectedSettlementCode === null) {
            this.dialogForm.controls['settlementCode'].setErrors({
              incorrect: true,
            });
          }
          return res !== null && res.length >= this.minLengthTerm;
        }),
        debounceTime(500),
        distinctUntilChanged(),
        tap(() => {
          this.filteredSettlements = [];
          this.isLoading = true;
        }),
        switchMap((value) =>
          this.settlementService.searchSettlements(value).pipe(
            catchError(() => {
              return of(undefined);
            }),
            finalize(() => {
              this.isLoading = false;
            })
          )
        )
      )
      .subscribe((data: any) => {
        this.filteredSettlements = data;
      });
  }

  onSubmit() {
    const data = {
      ...this.dialogForm.value,
      settlementCode: this.selectedSettlementCode,
    };
    console.log(data);
    this.dialogRef.close(data);
  }
}
