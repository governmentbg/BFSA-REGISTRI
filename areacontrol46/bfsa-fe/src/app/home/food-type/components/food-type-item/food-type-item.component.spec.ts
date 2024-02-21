import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { RouterTestingModule } from '@angular/router/testing';

import { FoodTypeItemComponent } from './food-type-item.component';

describe('FoodTypeItemComponent', () => {
  let component: FoodTypeItemComponent;
  let fixture: ComponentFixture<FoodTypeItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FoodTypeItemComponent],
      imports: [
        HttpClientModule,
        RouterTestingModule,
        MatSnackBarModule,
        MatDialogModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FoodTypeItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
