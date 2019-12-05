import {Component, OnInit} from '@angular/core';
import {Monitor, MonitorType} from '../../models/monitor';
import {MonitorsService} from '../../services/monitors.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';

/**
 * Component carrying the monitors
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  monitors: Monitor[];

  constructor(private monitorService: MonitorsService, private alertService: AlertService, private router: Router) {
    // Get monitors.
    this.monitorService.getMonitors().subscribe(data => {
      this.monitors = data as Monitor[];
    }, (error: HttpErrorResponse) => {
      this.alertService.error(error);
    });
  }

  ngOnInit() {
  }

  isHTTPMonitor(monitor: Monitor): boolean {
    return monitor.type === MonitorType.HTTP_MONITOR;
  }

  isSocketMonitor(monitor: Monitor): boolean {
    return monitor.type === MonitorType.SOCKET_MONITOR;
  }

  // Refresh hack
  refresh() {
    this.router.navigateByUrl('/createMonitor', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/']);
      }
    );
  }

  monitoringIntervalInSecToMonitoringIntervalInText(seconds: number | string) {
    seconds = Number(seconds);
    const minutes = seconds / 60;
    if (minutes <= 120) {
      return `${minutes} mins`;
    }
    return `${minutes / 60} hours`;
  }

  deleteMonitor(monitorId: number) {
    if (confirm('Are you sure to delete this Monitor')) {
      // Delete events created by logged in user.
      this.monitorService.deleteEvent(monitorId).subscribe(data => {
        this.alertService.success('Monitor deleted successfully!', true);
        this.refresh();
      }, error => this.alertService.error(error));
    }
  }

}
