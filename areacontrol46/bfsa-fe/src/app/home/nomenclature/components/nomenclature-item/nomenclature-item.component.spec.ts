import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { NomenclatureItemComponent } from './nomenclature-item.component';

describe('NomenclatureItemComponent', () => {
  let component: NomenclatureItemComponent;
  let fixture: ComponentFixture<NomenclatureItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [NomenclatureItemComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NomenclatureItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
