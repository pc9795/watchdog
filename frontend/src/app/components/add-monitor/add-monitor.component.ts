import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MonitorsService} from '../../services/monitors.service';
import {Router} from '@angular/router';
import {AlertService} from '../../services/alert.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Monitor, MonitorType} from '../../models/monitor';

@Component({
  selector: 'app-add-monitor',
  templateUrl: './add-monitor.component.html',
  styleUrls: ['./add-monitor.component.css']
})
export class AddMonitorComponent implements OnInit {

  monitorForm: FormGroup;
  submitted = false;
  loading = false;
  monitorTypes: string[];

  constructor(private formBuilder: FormBuilder, private monitorService: MonitorsService, private router: Router,
              private alertService: AlertService) {
    this.monitorTypes = Object.keys(MonitorType).map(obj => MonitorType[obj]);
  }

  // getter for easy access to form fields
  get f() {
    return this.monitorForm.controls;
  }

  ngOnInit() {
    // Initialize a form
    this.monitorForm = this.formBuilder.group({
      type: ['', Validators.required],
      name: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(50)]],
      ipOrHost: ['', [Validators.required]],
      monitoringInterval: ['', []],
      expectedStatusCode: ['', []],
      socketPort: ['', []]
    });
  }

  formatMonitoringInterval(valueStr: string): string {
    const value = Number(valueStr);
    if (value >= 5 && value <= 120) {
      return `${value} mins`;
    }
    return `${value - 120} hours`;
  }

  getMonitoringIntervalInSecs(valueStr: string) {
    const value = Number(valueStr);
    console.log(value);
    if (value >= 5 && value <= 120) {
      return value * 60;
    }
    return (value - 120) * 60 * 60;
  }

  isHTTPMonitor(value: string): boolean {
    return value === MonitorType.HTTP_MONITOR;
  }

  isSocketMonitor(value: string): boolean {
    return value === MonitorType.SOCKET_MONITOR;
  }

  // Adds an monitor
  onSubmit() {
    this.submitted = true;

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
    console.log(this.monitorForm.get('monitoringInterval').value);

    // Hits backend api to create monitor.
    this.loading = true;
    const monitor = new Monitor(-1, this.monitorForm.get('name').value, this.monitorForm.get('ipOrHost').value,
      this.getMonitoringIntervalInSecs(this.monitorForm.get('monitoringInterval').value), this.monitorForm.get('type').value,
      this.monitorForm.get('expectedStatusCode').value, this.monitorForm.get('socketPort').value);

    this.monitorService.createMonitor(monitor).subscribe(
      data => {
        this.alertService.success('Monitor created successfully', true);
        this.router.navigate(['/']);
      }, (error: HttpErrorResponse) => {
        this.alertService.error(error);
      }
    );
    this.loading = false;
  }


}
