import {Component, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Monitor, MonitorType} from '../../models/monitor';
import {Router} from '@angular/router';
import {MonitorsService} from '../../services/monitors.service';
import {AlertService} from '../../services/alert.service';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'app-view-monitor',
  templateUrl: './view-monitor.component.html',
  styleUrls: ['./view-monitor.component.css']
})
export class ViewMonitorComponent implements OnInit {

  monitor: Monitor; // Passed data
  viewMonitorForm: FormGroup;
  submitted = false;
  loading = false;
  monitorTypes: string[];

  constructor(private router: Router, private formBuilder: FormBuilder, private monitorService: MonitorsService,
              private alertService: AlertService) {
    // Receive information from other component.
    const state = this.router.getCurrentNavigation().extras.state;
    if (state) {
      this.monitor = state.monitor;
    }
    this.monitorTypes = Object.keys(MonitorType).map(obj => MonitorType[obj]);
  }

  // Utility method for angular form controls
  get f() {
    return this.viewMonitorForm.controls;
  }

  ngOnInit() {
    // Initialize the form
    this.viewMonitorForm = this.formBuilder.group({
      type: [this.monitor ? this.monitor.type : '', Validators.required],
      name: [this.monitor ? this.monitor.name : '', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      ipOrHost: [this.monitor ? this.monitor.ipOrHost : '', [Validators.required]],
      monitoringInterval: [this.monitor ? this.monitoringIntervalInSecToRange(this.monitor.monitoringInterval) : '', []],
      expectedStatusCode: [this.monitor ? this.monitor.expectedStatusCode : '', []],
      socketPort: [this.monitor ? this.monitor.socketPort : '', []]
    });
  }

  rangeToMonitoringIntervalInSec(valueStr: string) {
    const value = Number(valueStr);
    if (value >= 5 && value <= 120) {
      return value * 60;
    }
    return (value - 120) * 60 * 60;
  }

  rangeToMonitoringIntervalInText(valueStr: string): string {
    const value = Number(valueStr);
    if (value >= 5 && value <= 120) {
      return `${value} mins`;
    }
    return `${value - 120} hours`;
  }

  monitoringIntervalInSecToRange(secs: number) {
    const minutes = secs / 60;
    if (minutes >= 5 && minutes <= 120) {
      return minutes;
    }
    return minutes / 60;
  }

  isHTTPMonitor(value: string): boolean {
    return value === MonitorType.HTTP_MONITOR;
  }

  isSocketMonitor(value: string): boolean {
    return value === MonitorType.SOCKET_MONITOR;
  }

  // If user wants to edit an event.
  onSubmit() {
    this.submitted = true;

    // Invalid form
    if (this.viewMonitorForm.invalid) {
      return;
    }

    this.loading = true;
    const monitor = new Monitor(this.monitor.id, this.viewMonitorForm.get('name').value, this.viewMonitorForm.get('ipOrHost').value,
      this.rangeToMonitoringIntervalInSec(this.viewMonitorForm.get('monitoringInterval').value), this.viewMonitorForm.get('type').value,
      this.viewMonitorForm.get('expectedStatusCode').value, this.viewMonitorForm.get('socketPort').value);

    this.monitorService.updateMonitor(monitor, monitor.id).subscribe(
      data => {
        this.alertService.success('Monitor updated successfully', true);
        // Go to home page
        this.router.navigate(['/']);
      }, (error: HttpErrorResponse) => {
        this.alertService.error(error);
      }
    );
    this.loading = false;
  }
}
