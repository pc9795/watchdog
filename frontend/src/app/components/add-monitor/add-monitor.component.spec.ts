import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddMonitorComponent } from './add-monitor.component';

describe('AddMonitorComponent', () => {
  let component: AddMonitorComponent;
  let fixture: ComponentFixture<AddMonitorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddMonitorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
