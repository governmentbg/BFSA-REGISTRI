import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { NomenclatureService } from './nomenclature.service';

describe('NomenclatureService', () => {
  let service: NomenclatureService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(NomenclatureService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
