import { Component, OnInit } from '@angular/core';
import { Validators, FormControl } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  finalize,
  Subject,
  switchMap,
  tap,
  throwError,
} from 'rxjs';
import { ContractorService } from 'src/app/home/contractor/services/contractor.service';
import { ContractorInterface } from 'src/app/home/contractor/interfaces/contractor-interface';

@Component({
  selector: 'app-register-search',
  templateUrl: './register-search.component.html',
  styleUrls: ['./register-search.component.scss'],
})
export class RegisterSearchComponent implements OnInit {
  public isLoading = false;
  public contractors: ContractorInterface[];
  public latestSearch = new Subject<string>();
  public registerControl = new FormControl(null);

  constructor(
    private readonly router: Router,
    private readonly contractorService: ContractorService,
    private readonly _snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.registerControl.addValidators([Validators.required]);

    this.latestSearch
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        filter((term) => term.trim() !== '' && term.length > 1),
        tap(() => {
          this.isLoading = true;
          this.contractors = [];
        }),
        switchMap((q) => {
          return this.contractorService.getContractors(q).pipe(
            catchError((error) => throwError(() => error)),
            finalize(() => {
              this.isLoading = false;
            })
          );
        })
      )
      .subscribe((contractors: ContractorInterface[]) => {
        if (!contractors.length) {
          this._snackBar.open('Not found', '', { duration: 1000 });
          this.registerControl.setErrors({ required: true });
        } else {
          console.log(contractors);
          this.contractors = contractors;
        }
      });
  }

  search(el: HTMLInputElement) {
    this.latestSearch.next(el.value.trim());
  }

  onSelected(id: string) {
    console.log(id);
    this.router.navigate(['app/registers', id]);
  }

  clearSelection() {
    this.registerControl.setValue(null);
  }
}
