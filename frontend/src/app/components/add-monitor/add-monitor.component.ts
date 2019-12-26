import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MonitorsService} from '../../services/monitors.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Monitor, MonitorType} from '../../models/monitor';
import {Utils} from '../../utils';

/**
 * Component to add monitors
 */
@Component({
  selector: 'app-add-monitor',
  templateUrl: './add-monitor.component.html',
  styleUrls: ['./add-monitor.component.css']
})
export class AddMonitorComponent implements OnInit {

  monitorForm: FormGroup; // Form object
  submitted = false; // Signifies form is submitted
  loading = false; // Signifies form is loading
  monitorTypes: string[]; // Types of monitor

  // To use utils in template we have to first save them in instance variables.
  minRange = Utils.minRange;
  maxRange = Utils.maxRange;
  rangeToMonitoringIntervalInText = Utils.rangeToMonitoringIntervalInText;
  isSocketMonitor = Utils.isSocketMonitor;
  isHTTPMonitor = Utils.isHTTPMonitor;

  constructor(private formBuilder: FormBuilder, private monitorService: MonitorsService, private router: Router,
              private alertService: AlertService) {
    this.monitorTypes = Object.keys(MonitorType).map(obj => MonitorType[obj]);
  }

  /**
   * Getter for easy access to form fields
   */
  get f() {
    return this.monitorForm.controls;
  }

  /**
   * Initialization hook
   */
  ngOnInit() {
    // Initialize the form
    this.monitorForm = this.formBuilder.group({
      type: ['', Validators.required],
      name: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      ipOrHost: ['', [Validators.required]],
      monitoringInterval: [Utils.minRange, []],
      expectedStatusCode: ['', []],
      socketPort: ['', []]
    });
  }

  /**
   * Action on form submit
   */
  onSubmit() {
    this.submitted = true;

    // If form is invalid
    if (this.monitorForm.invalid) {
      Object.keys(this.monitorForm.controls).forEach(key => {
        const controlErrors = this.monitorForm.get(key).errors;
        if (controlErrors != null) {
          Object.keys(controlErrors).forEach(keyError => {
            console.log('Key control: ' + key + ', keyError: ' + keyError + ', err value: ', controlErrors[keyError]);
          });
        }
      });
      return;
    }

    this.loading = true;
    console.log(this.monitorForm.get('monitoringInterval').value);
    // Get the monitor object from the form
    const monitor = new Monitor(-1, this.monitorForm.get('name').value, this.monitorForm.get('ipOrHost').value,
      Utils.rangeToMonitoringIntervalInSec(this.monitorForm.get('monitoringInterval').value), this.monitorForm.get('type').value,
      this.monitorForm.get('expectedStatusCode').value, this.monitorForm.get('socketPort').value);
    // Hits backend api to create monitor.
    this.monitorService.createMonitor(monitor).subscribe(
      // Success
      data => {
        this.alertService.success('Monitor created successfully', true);
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
