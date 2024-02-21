import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';

import { ActivityGroupService } from './activity-group.service';

describe('ActivityGroupService', () => {
  let service: ActivityGroupService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule],
    });
    service = TestBed.inject(ActivityGroupService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
