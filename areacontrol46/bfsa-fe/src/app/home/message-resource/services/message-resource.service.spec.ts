import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { MessageResourceService } from './message-resource.service';

describe('MessageResourceService', () => {
  let service: MessageResourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(MessageResourceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
