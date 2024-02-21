import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NestedFoodTypesListComponent } from './nested-food-types-list.component';

describe('NestedFoodTypesListComponent', () => {
  let component: NestedFoodTypesListComponent;
  let fixture: ComponentFixture<NestedFoodTypesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NestedFoodTypesListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NestedFoodTypesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
