import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodTypesCheckboxListComponent } from './food-types-checkbox-list.component';

describe('FoodTypesCheckboxListComponent', () => {
  let component: FoodTypesCheckboxListComponent;
  let fixture: ComponentFixture<FoodTypesCheckboxListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FoodTypesCheckboxListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FoodTypesCheckboxListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
