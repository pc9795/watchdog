import {Component, OnInit} from '@angular/core';
import {Monitor} from '../../models/monitor';
import {MonitorsService} from '../../services/monitors.service';
import {HttpErrorResponse} from '@angular/common/http';
import {AlertService} from '../../services/alert.service';
import {Router} from '@angular/router';
import {MonitorLog} from '../../models/monitor-log';

enum MonitorStatus {
  WARMING,
  WORKING,
  NOT_WORKING
}

/**
 * Component for home page which contains the list of all monitors. You can create/edit/delete monitors through this screen.
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  monitors: Monitor[]; // Monitors for the user
  monitorStatus = new Map(); // Map to store the status of each monitor

  constructor(private monitorService: MonitorsService, private alertService: AlertService, private router: Router) {
    // Get monitors.
    this.monitorService.getMonitors().subscribe(
      // Success
      data => {
        this.monitors = data as Monitor[];
        // Initially load all the monitors in the map
        this.monitors.map(obj => this.monitorStatus.set(obj.id, MonitorStatus.WARMING));
        // Fetch status of all the monitors
        this.monitors.forEach(obj => this.getStatus(obj.id));
      },
      // Error
      (error: HttpErrorResponse) => {
        this.alertService.error(error);
      });
  }

  /**
   * Get status of a particular monitor
   */
  getStatus(monitorId: number) {
    this.monitorService.getMonitorStatus(monitorId).subscribe(
      // Success
      data2 => {
        // Update status
        this.monitorStatus.set((data2 as MonitorLog).monitorId,
          (data2 as MonitorLog).status ? MonitorStatus.WORKING : MonitorStatus.NOT_WORKING);
      },
      // Error
      error2 => this.alertService.error(error2)
    );
  }

  /**
   * Refresh hack for Angular
   */
  refresh() {
    // Navigating to some other url and then coming back to orignal. It will be so fast that not be observed.
    this.router.navigateByUrl('/createMonitor', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/']);
      }
    );
  }

  /**
   * Get html code corresponding to monitor status.
   */
  getMonitorStatus(monitorId: number): string {
    switch (this.monitorStatus.get(monitorId)) {
      case MonitorStatus.WARMING:
        return '<i class="fa fa-spin fa-spinner"></i>';
      case MonitorStatus.WORKING:
        return '<i class="fa fa-check green"></i>';
      case MonitorStatus.NOT_WORKING:
        return '<i class="fa fa-close red"></i>';
    }
    return '';
  }

  /**
   * Convert value in seconds to text.
   */
  monitoringIntervalInSecToMonitoringIntervalInText(seconds: number | string) {
    seconds = Number(seconds);
    const minutes = seconds / 60;
    if (minutes <= 120) {
      return `${minutes} mins`;
    }
    return `${minutes / 60} hours`;
  }

  /**
   * Handle delete monitor button
   */
  deleteMonitor(monitorId: number) {
    // Confirm dialog. Can use a fancy modal for future.
    if (confirm('Are you sure to delete this Monitor')) {
      // Delete events created by logged in user.
      this.monitorService.deleteEvent(monitorId).subscribe(
        // Success
        data => {
          this.alertService.success('Monitor deleted successfully!', true);
          this.refresh();
        },
        // Error
        error => this.alertService.error(error));
    }
  }

  /**
   * Initialiaztion hook
   */
  ngOnInit(): void {
  }

}
