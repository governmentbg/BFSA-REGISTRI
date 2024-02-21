import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RegisterSearchComponent } from './register-search.component';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

describe('RegisterSearchComponent', () => {
  let component: RegisterSearchComponent;
  let fixture: ComponentFixture<RegisterSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RegisterSearchComponent],
      imports: [
        HttpClientModule,
        MatSnackBarModule,
        NoopAnimationsModule,
        MatAutocompleteModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
