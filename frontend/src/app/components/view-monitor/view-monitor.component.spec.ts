import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewMonitorComponent } from './view-monitor.component';

describe('ViewMonitorComponent', () => {
  let component: ViewMonitorComponent;
  let fixture: ComponentFixture<ViewMonitorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ViewMonitorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ViewMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
