import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { SettlementService } from './settlement.service';

describe('SettlementService', () => {
  let service: SettlementService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(SettlementService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
