import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { FoodTypeService } from './food-type.service';

describe('FoodTypeService', () => {
  let service: FoodTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(FoodTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
