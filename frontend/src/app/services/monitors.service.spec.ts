import { TestBed } from '@angular/core/testing';

import { MonitorsService } from './monitors.service';

describe('MonitorsService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: MonitorsService = TestBed.get(MonitorsService);
    expect(service).toBeTruthy();
  });
});
