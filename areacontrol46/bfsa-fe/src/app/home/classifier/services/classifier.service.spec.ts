import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { ClassifierService } from './classifier.service';

describe('ClassifierService', () => {
  let service: ClassifierService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(ClassifierService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
