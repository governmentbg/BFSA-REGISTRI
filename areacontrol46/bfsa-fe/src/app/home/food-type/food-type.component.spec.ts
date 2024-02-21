import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { FoodTypeComponent } from './food-type.component';

describe('FoodTypeComponent', () => {
  let component: FoodTypeComponent;
  let fixture: ComponentFixture<FoodTypeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [FoodTypeComponent],
      imports: [RouterTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(FoodTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
