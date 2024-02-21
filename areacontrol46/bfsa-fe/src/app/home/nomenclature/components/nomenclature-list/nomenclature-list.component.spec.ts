import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { NomenclatureListComponent } from './nomenclature-list.component';

describe('NomenclatureListComponent', () => {
  let component: NomenclatureListComponent;
  let fixture: ComponentFixture<NomenclatureListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NomenclatureListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatIconModule,
        MatSnackBarModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NomenclatureListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
