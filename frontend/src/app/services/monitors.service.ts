import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Monitor} from '../models/monitor';

/**
 * Interacts with server to get monitor REST resource
 */
@Injectable({
  providedIn: 'root'
})
export class MonitorsService {

  constructor(private httpClient: HttpClient) {
  }

  getMonitors() {
    return this.httpClient.get(`${environment.server}api/v1/monitors`, {withCredentials: true});
  }

  createMonitor(monitor: Monitor) {
    return this.httpClient.post(`${environment.server}api/v1/monitors`, monitor, {withCredentials: true});
  }

  updateMonitor(monitor: Monitor, monitorId: number) {
    return this.httpClient.put(`${environment.server}api/v1/monitors/${monitorId}`, monitor, {withCredentials: true});
  }

  deleteEvent(monitorId: number) {
    return this.httpClient.delete(`${environment.server}api/v1/monitors/${monitorId}`, {withCredentials: true});
  }

  getMonitorStatus(monitorId: number) {
    return this.httpClient.get(`${environment.server}api/v1/monitors/${monitorId}/status`, {withCredentials: true});
  }

  getMonitorLogs(monitorId: number) {
    return this.httpClient.get(`${environment.server}api/v1/monitors/${monitorId}/logs`, {withCredentials: true});
  }
}
