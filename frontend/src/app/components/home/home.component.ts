import {Component, OnInit} from '@angular/core';
import {Monitor, MonitorType} from '../../models/monitor';
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
 * Component carrying the monitors
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  monitors: Monitor[];
  monitorStatus = new Map();

  constructor(private monitorService: MonitorsService, private alertService: AlertService, private router: Router) {
    // Get monitors.
    this.monitorService.getMonitors().subscribe(data => {
      this.monitors = data as Monitor[];
      this.monitors.map(obj => this.monitorStatus.set(obj.id, MonitorStatus.WARMING));
      this.monitors.forEach(
        obj => {
          this.monitorService.getMonitorStatus(obj.id).subscribe(
            data2 => {
              this.monitorStatus.set((data2 as MonitorLog).monitorId,
                (data2 as MonitorLog).status ? MonitorStatus.WORKING : MonitorStatus.NOT_WORKING);
            }, error2 => this.alertService.error(error2)
          );
        }
      );
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

  getStatus(monitorId: number) {
    this.monitorService.getMonitorStatus(monitorId).subscribe(
      data2 => {
        this.monitorStatus.set((data2 as MonitorLog).monitorId,
          (data2 as MonitorLog).status ? MonitorStatus.WORKING : MonitorStatus.NOT_WORKING);
      }, error2 => this.alertService.error(error2)
    );
  }

  // Refresh hack
  refresh() {
    this.router.navigateByUrl('/createMonitor', {skipLocationChange: true}).then(
      () => {
        this.router.navigate(['/']);
      }
    );
  }

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
