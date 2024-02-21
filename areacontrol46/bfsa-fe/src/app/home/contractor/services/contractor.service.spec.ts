import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { ContractorService } from './contractor.service';

describe('ContractorService', () => {
  let service: ContractorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(ContractorService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
