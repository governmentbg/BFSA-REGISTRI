import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PermissionsComponentTable } from './permissions-table.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

describe('PermissionsComponent', () => {
  let component: PermissionsComponentTable;
  let fixture: ComponentFixture<PermissionsComponentTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PermissionsComponentTable],
      imports: [
        NoopAnimationsModule,
        MatSnackBarModule,
        MatDialogModule,
        MatAutocompleteModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(PermissionsComponentTable);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
