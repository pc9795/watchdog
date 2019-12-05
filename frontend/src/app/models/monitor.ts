export class Monitor {
  constructor(
    public id: number, public name: string, public ipOrHost: string, public monitoringInterval: number, public type: MonitorType,
    public expectedStatusCode?: number, public socketPort?: number
  ) {
  }
}

export enum MonitorType {
  PING_MONITOR = 'PingMonitor',
  HTTP_MONITOR = 'HttpMonitor',
  SOCKET_MONITOR = 'SocketMonitor'
}
