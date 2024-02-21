import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  MatSnackBar,
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
} from '@angular/material/snack-bar';
import { Meta, Title } from '@angular/platform-browser';
import { TranslateService } from '@ngx-translate/core';
import { map, Observable, take } from 'rxjs';
import { AuthenticationService } from 'src/app/services/auth.service';
import { LanguageService } from 'src/app/services/language.service';
import { LanguageInterface } from 'src/app/shared/interfaces/language-interface';

@Component({
  selector: 'app-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.scss']
})
export class SigninComponent implements OnInit {
  hide = true;
  submitted = false;

  horizontalPosition: MatSnackBarHorizontalPosition = 'center';
  verticalPosition: MatSnackBarVerticalPosition = 'top';

  // themes = [
  //   {
  //     name: 'Light Theme',
  //     abbreviation: 'light',
  //   },
  //   {
  //     name: 'Dark Theme',
  //     abbreviation: 'dark',
  //   },
  //   {
  //     name: 'Contrast Theme',
  //     abbreviation: 'contrast',
  //   },
  // ];

  // fontSizes = [
  //   {
  //     name: 'Normal',
  //     abbreviation: 'normal',
  //   },
  //   {
  //     name: 'Medium',
  //     abbreviation: 'medium',
  //   },
  //   {
  //     name: 'Large',
  //     abbreviation: 'large',
  //   },
  // ];

  languages$: Observable<LanguageInterface[]>;

  loginForm = this.fb.group({
    username: ['admin', [Validators.required]],
    password: ['admin', [Validators.required, Validators.minLength(5)]],
    // theme: [
    //   localStorage.getItem('theme')
    //     ? localStorage.getItem('theme')
    //     : this.themes[0].abbreviation,
    // ],
    // fontSize: [
    //   localStorage.getItem('fontSize')
    //     ? localStorage.getItem('fontSize')
    //     : this.fontSizes[0].abbreviation,
    // ],
    language: null,
    remember: null,
  });

  constructor(
    public translate: TranslateService,
    private languageService: LanguageService,
    private fb: UntypedFormBuilder,
    private authenticationService: AuthenticationService,
    private title: Title,
    private meta: Meta,
    private _snackBar: MatSnackBar
  ) {
    this.loginForm.get('language')?.valueChanges.subscribe((lang) => {
      localStorage.setItem('lang', lang);
      this.translate.use(lang);
    });

    // this.loginForm.get('theme')?.valueChanges.subscribe((value) => {
    //   console.log(value);
    //   localStorage.setItem('theme', value);
    //   if (value === 'dark') {
    //     document.body.classList.remove('contrast-theme-mode');
    //     document.body.classList.add('dark-theme-mode');
    //   }
    //   if (value === 'contrast') {
    //     document.body.classList.remove('dark-theme-mode');
    //     document.body.classList.add('contrast-theme-mode');
    //   }
    //   if (value === 'light') {
    //     document.body.classList.remove('dark-theme-mode');
    //     document.body.classList.remove('contrast-theme-mode');
    //   }
    // });
    // this.loginForm.get('fontSize')?.valueChanges.subscribe((value) => {
    //   console.log(value);
    //   localStorage.setItem('fontSize', value);
    //   if (value === 'medium') {
    //     document.body.classList.remove('font-size-large');
    //     document.body.classList.add('font-size-medium');
    //   }
    //   if (value === 'large') {
    //     document.body.classList.remove('font-size-medium');
    //     document.body.classList.add('font-size-large');
    //   }
    //   if (value === 'normal') {
    //     document.body.classList.remove('font-size-medium');
    //     document.body.classList.remove('font-size-large');
    //   }
    // });

    title.setTitle('Login');
  }

  ngOnInit(): void {
    this.fetchLanguages();
  }

  onSubmit(): void {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    console.log(this.loginForm.value);

    this.authenticationService
      .login(
        this.loginForm.get('username')!.value,
        this.loginForm.get('password')!.value, 'signin'
      )
      .pipe(take(1))
      .subscribe({
        next: (v) => console.log(v),
        error: (err) => {
          console.error(err);
          this._snackBar.open(
            err.error.error ? err.error.error : err.error,
            'Close',
            {
              duration: 3000,
              horizontalPosition: this.horizontalPosition,
              verticalPosition: this.verticalPosition,
            }
          );
        },
      });
  }

  fetchLanguages() {
    const lang = localStorage.getItem('lang');
    this.languages$ = this.languageService.getLangs().pipe(
      map((data) => {
        const languages = data.results.map((l) => l.languageId);
        const defaultLanguage = data.results
          .filter((l) => l.main === true)
          .map((l) => l.languageId)
          .join();
        this.translate.addLangs(languages);
        this.loginForm.get('language')?.patchValue(!lang ? defaultLanguage : lang);
        return data.results;
      })
    );
  }
}
