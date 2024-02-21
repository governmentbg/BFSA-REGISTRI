import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule } from '@angular/material/snack-bar';

import { FoodTypeListComponent } from './food-type-list.component';

describe('FoodTypeListComponent', () => {
  let component: FoodTypeListComponent;
  let fixture: ComponentFixture<FoodTypeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FoodTypeListComponent],
      imports: [
        HttpClientModule,
        MatDialogModule,
        MatIconModule,
        MatSnackBarModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(FoodTypeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
