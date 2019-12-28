import {Component, OnChanges, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Monitor, MonitorType} from '../../models/monitor';
import {Router} from '@angular/router';
import {MonitorsService} from '../../services/monitors.service';
import {AlertService} from '../../services/alert.service';
import {HttpErrorResponse} from '@angular/common/http';
import {MonitorLog} from '../../models/monitor-log';
import {Utils} from '../../utils';

/**
 * Component to view monitor, its recent logs and edit it.
 */
@Component({
  selector: 'app-view-monitor',
  templateUrl: './view-monitor.component.html',
  styleUrls: ['./view-monitor.component.css']
})
export class ViewMonitorComponent implements OnInit {

  monitor: Monitor; // Passed monitor details
  viewMonitorForm: FormGroup; // Form object
  submitted = false; // Signifies that form is submitted
  loading = false; // Signifies that form is loading
  monitorTypes: string[]; // Monitor types
  monitorLogs: MonitorLog[]; // Monitor logs for the monitor

  // To use utils in template we have to first save them in instance variables.
  minRange = Utils.minRange;
  maxRange = Utils.maxRange;
  rangeToMonitoringIntervalInText = Utils.rangeToMonitoringIntervalInText;
  isSocketMonitor = Utils.isSocketMonitor;
  isHTTPMonitor = Utils.isHTTPMonitor;

  constructor(private router: Router, private formBuilder: FormBuilder, private monitorService: MonitorsService,
              private alertService: AlertService) {

    // Receive information from other component.
    const state = this.router.getCurrentNavigation().extras.state;

    if (state) {
      this.monitor = state.monitor;
      // Get monitor logs.
      this.monitorService.getMonitorLogs(this.monitor.id).subscribe(
        // Success
        data => {
          this.monitorLogs = data as MonitorLog[];
        },
        // Error
        (error: HttpErrorResponse) => {
          this.alertService.error(error);
        });
    }

    this.monitorTypes = Object.keys(MonitorType).map(obj => MonitorType[obj]);
  }

  /**
   * Getter for easy access to form fields
   */
  get f() {
    return this.viewMonitorForm.controls;
  }

  /**
   * Initialization hook
   */
  ngOnInit() {
    // Initialize the form
    this.viewMonitorForm = this.formBuilder.group({
      type: [this.monitor ? this.monitor.type : '', Validators.required],
      name: [this.monitor ? this.monitor.name : '', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      ipOrHost: [this.monitor ? this.monitor.ipOrHost : '', [Validators.required]],
      monitoringInterval: [this.monitor ? Utils.monitoringIntervalInSecToRange(this.monitor.monitoringInterval) : '', []],
      expectedStatusCode: [this.monitor ? this.monitor.expectedStatusCode : '', []],
      socketPort: [this.monitor ? this.monitor.socketPort : '', []]
    });
  }

  /**
   * Action on submitting the form
   */
  onSubmit() {
    this.submitted = true;

    // Invalid form
    if (this.viewMonitorForm.invalid) {
      return;
    }

    this.loading = true;
    // Get monitor object from form
    const monitor = new Monitor(this.monitor.id, this.viewMonitorForm.get('name').value, this.viewMonitorForm.get('ipOrHost').value,
      Utils.rangeToMonitoringIntervalInSec(this.viewMonitorForm.get('monitoringInterval').value), this.viewMonitorForm.get('type').value,
      this.viewMonitorForm.get('expectedStatusCode').value, this.viewMonitorForm.get('socketPort').value);

    this.monitorService.updateMonitor(monitor, monitor.id).subscribe(
      // Success
      data => {
        this.alertService.success('Monitor updated successfully', true);
        // Go to home page
        this.router.navigate(['/']);
      },
      // Error
      (error: HttpErrorResponse) => {
        this.alertService.error(error);
      }
    );
    this.loading = false;
  }
}
